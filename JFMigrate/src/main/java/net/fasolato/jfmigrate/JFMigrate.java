package net.fasolato.jfmigrate;

import net.fasolato.jfmigrate.builders.Change;
import net.fasolato.jfmigrate.builders.Data;
import net.fasolato.jfmigrate.internal.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.sql.*;
import java.util.*;

public class JFMigrate {
    private static Logger log = LogManager.getLogger(JFMigrate.class);

    private List<String> packages;
    private SqlDialect dialect;
    private String schema;

    public JFMigrate() {
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("jfmigrate.properties");
        try {
            properties.load(stream);
            String configDialect = properties.getProperty("jfmigrate.db.dialect");

            dialect = SqlDialect.H2;
            if (configDialect.equalsIgnoreCase("h2")) {
                dialect = SqlDialect.H2;
            } else if (configDialect.equalsIgnoreCase("sqlserver")) {
                dialect = SqlDialect.SQL_SERVER;
            } else if (configDialect.equalsIgnoreCase("pgsql")) {
                dialect = SqlDialect.PGSQL;
            } else if (configDialect.equalsIgnoreCase("mysql")) {
                dialect = SqlDialect.MYSQL;
            }
        } catch (IOException e) {
            log.error(e);
            throw new JFException("Error reading properties file", e);
        }
        packages = new ArrayList<String>();
    }

    public void registerPackage(String pkg) {
        packages.add(pkg);
    }

    public void registerPackage(Class<?> clazz) {
        packages.add(clazz.getPackage().getName());
    }

    private IDialectHelper getDialectHelper() {
        switch (dialect) {
            case SQL_SERVER:
                return new SqlServerDialectHelper();
            case H2:
                return new H2DialectHelper();
            case PGSQL:
                return new PGSqlDialectHelper();
            case MYSQL:
                return new MysqlDialectHelper(schema);
            default:
                throw new NotImplementedException();
        }
    }

    private long getDatabaseVersion(IDialectHelper helper, Connection conn) throws SQLException {
        String versionTableExistence = helper.getDatabaseVersionTableExistenceCommand();
        PreparedStatement st = new LoggablePreparedStatement(conn, versionTableExistence);
        log.info("Executing{}{}", System.lineSeparator(), st);
        ResultSet rs = st.executeQuery();
        boolean exists = true;
        if (!rs.next()) {
            exists = false;
        } else {
            if (rs.getInt(1) == 0) {
                exists = false;
            }
        }
        if (!exists) {
            createVersionTable(helper, conn);
            return -1;
        }

        String currentVersionCommand = helper.getDatabaseVersionCommand();

        long dbVersion = -1;
        st = new LoggablePreparedStatement(conn, currentVersionCommand);
        log.info("Executing{}{}", System.lineSeparator(), st);

        rs = st.executeQuery();
        if (rs.next()) {
            dbVersion = rs.getLong(1);
        }
        return dbVersion;
    }

    private void createVersionTable(IDialectHelper helper, Connection conn) throws SQLException {
        String createCommand = helper.getVersionTableCreationCommand();

        PreparedStatement st = new LoggablePreparedStatement(conn, createCommand);
        log.info("Executing{}{}", System.lineSeparator(), st);
        st.executeUpdate();

    }

    public void migrateUp() throws Exception {
        migrateUp(-1, null, false);
    }

    public void migrateUp(Writer out) throws Exception {
        migrateUp(-1, out, false);
    }

    public void migrateUp(Writer out, boolean createVersionInfoTable) throws Exception {
        migrateUp(-1, out, createVersionInfoTable);
    }

    public void migrateUp(int startMigrationNumber, Writer out, boolean createVersionInfoTable) throws Exception {
        IDialectHelper helper = getDialectHelper();
        DatabaseHelper dbHelper = new DatabaseHelper();

        Connection conn = null;
        try {
            conn = dbHelper.getConnection();
            conn.setAutoCommit(false);

            long dbVersion = 0;
            if (out == null) {
                dbVersion = getDatabaseVersion(helper, conn);
            } else if (createVersionInfoTable) {
                out.write("-- Version table");
                out.write(System.lineSeparator());
                out.write(System.lineSeparator());
                out.write(helper.getVersionTableCreationCommand());
                out.write(System.lineSeparator());
                out.write(System.lineSeparator());
                out.write("--------------------------------------------");
                out.write(System.lineSeparator());
                out.write(System.lineSeparator());
                out.write(System.lineSeparator());
            }
            log.info("Current database version: {}", dbVersion);

            for (String p : packages) {
                log.debug("Migrating up from package {}", p);

                List<JFMigrationClass> migrations = ReflectionHelper.getAllMigrations(p);
                Collections.sort(migrations, new Comparator<JFMigrationClass>() {
                    public int compare(JFMigrationClass jfMigrationClass, JFMigrationClass t1) {
                        return Long.compare(jfMigrationClass.getMigrationNumber(), t1.getMigrationNumber());
                    }
                });

                for (JFMigrationClass m : migrations) {
                    if (m.getMigrationNumber() > dbVersion && (startMigrationNumber == -1 || m.getMigrationNumber() >= startMigrationNumber)) {
                        log.debug("Applying migration UP {}({})", m.getMigrationName(), m.getMigrationNumber());
                        m.up();

                        Savepoint save = null;
                        String[] scriptVersionCheck = null;
                        if (out == null) {
                            save = conn.setSavepoint();
                        } else {
                            out.write(String.format("-- Migration %s(%s)", m.getMigrationName(), m.getMigrationNumber()));
                            out.write(System.lineSeparator());
                            out.write(System.lineSeparator());

                            scriptVersionCheck = helper.getScriptCheckMigrationUpVersionCommand();
                            if (scriptVersionCheck != null) {
                                out.write(scriptVersionCheck[0].replaceAll("\\?", String.valueOf(m.getMigrationNumber())));
                                out.write(System.lineSeparator());
                            }
                        }
                        PreparedStatement st;
                        try {
                            for (Change c : m.migration.getChanges()) {
                                if (Data.class.isAssignableFrom(c.getClass())) {
                                    Data d = (Data) c;

                                    for (Pair<String, Object[]> commands : d.getSqlCommand(helper)) {
                                        st = new LoggablePreparedStatement(conn, commands.getA());
                                        for (int iv = 0; iv < commands.getB().length; iv++) {
                                            st.setObject(iv + 1, commands.getB()[iv]);
                                        }
                                        log.info("Executing{}{}", System.lineSeparator(), st);
                                        if (out == null) {
                                            st.executeUpdate();
                                        } else {
                                            out.write(st.toString().trim());
                                            out.write(System.lineSeparator());
                                            out.write(System.lineSeparator());
                                        }
                                    }
                                } else {
                                    for (Pair<String, Object[]> commands : c.getSqlCommand(helper)) {
                                        st = new LoggablePreparedStatement(conn, commands.getA());
                                        log.info("Executing{}{}", System.lineSeparator(), st);
                                        if (out == null) {
                                            st.executeUpdate();
                                        } else {
                                            out.write(st.toString().trim());
                                            out.write(System.lineSeparator());
                                            out.write(System.lineSeparator());
                                        }
                                    }
                                }
                            }

                            String migrationVersionCommand = helper.getInsertNewVersionCommand();
                            st = new LoggablePreparedStatement(conn, migrationVersionCommand);
                            st.setLong(1, m.getMigrationNumber());
                            st.setString(2, m.getMigrationName());
                            log.info("Executing{}{}", System.lineSeparator(), st);
                            if (out == null) {
                                st.executeUpdate();
                            } else {
                                out.write(st.toString().trim());
                                out.write(System.lineSeparator());
                                out.write(System.lineSeparator());
                            }

                            if (out != null) {
                                if (scriptVersionCheck != null) {
                                    out.write(scriptVersionCheck[1]);
                                    out.write(System.lineSeparator());
                                }
                                out.write("--------------------------------------------");
                                out.write(System.lineSeparator());
                                out.write(System.lineSeparator());
                                out.write(System.lineSeparator());
                            }
                            log.debug("Applied migration {}", m.getClass().getSimpleName());
                        } catch (Exception e) {
                            if (conn != null && save != null) {
                                conn.rollback(save);
                            }
                            throw e;
                        }
                    } else {
                        if (m.getMigrationNumber() <= dbVersion) {
                            log.info("Skipping migration {} because DB is newer", m.getMigrationNumber());
                        } else {
                            log.info("Skipping migration {} because lower than selected start migration number ({})", m.getMigrationNumber(), startMigrationNumber);
                        }
                    }
                }
            }

            if (conn != null) {
                conn.commit();
            }
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    log.error("Connection rolled back");
                } catch (Exception ex) {
                    log.error("Error while rolling back transaction", ex);
                }
            }
            log.error("Error executing query", e);
            throw e;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                log.error(ex);
            }
        }
    }

    public void migrateDown(int targetMigration) throws Exception {
        migrateDown(targetMigration, null);
    }

    public void migrateDown(int targetMigration, Writer out) throws Exception {
        IDialectHelper helper = getDialectHelper();
        DatabaseHelper dbHelper = new DatabaseHelper();

        Connection conn = null;
        try {
            conn = dbHelper.getConnection();
            conn.setAutoCommit(false);

            long dbVersion = Long.MAX_VALUE;
            if (out == null) {
                dbVersion = getDatabaseVersion(helper, conn);
            }
            log.info("Current database version: {}", dbVersion);

            if (dbVersion <= 0) {
                //No migration table or DB is at first migration, nothing to be done
                return;
            }

            for (String p : packages) {
                log.debug("Migrating down from package {}", p);

                List<JFMigrationClass> migrations = ReflectionHelper.getAllMigrations(p);
                Collections.sort(migrations, new Comparator<JFMigrationClass>() {
                    public int compare(JFMigrationClass jfMigrationClass, JFMigrationClass t1) {
                        return -1 * Long.compare(jfMigrationClass.getMigrationNumber(), t1.getMigrationNumber());
                    }
                });

                for (JFMigrationClass m : migrations) {
                    if (m.getMigrationNumber() <= dbVersion && m.getMigrationNumber() > targetMigration) {
                        log.debug("Applying migration DOWN {}({})", m.getMigrationName(), m.getMigrationNumber());
                        m.down();

                        Savepoint save = null;
                        String[] scriptVersionCheck = null;
                        if (out == null) {
                            save = conn.setSavepoint();
                        } else {
                            out.write(String.format("-- Migration down %s(%s)", m.getMigrationName(), m.getMigrationNumber()));
                            out.write(System.lineSeparator());
                            out.write(System.lineSeparator());
                        }

                        scriptVersionCheck = helper.getScriptCheckMigrationDownVersionCommand();
                        if (out != null && scriptVersionCheck != null) {
                            out.write(scriptVersionCheck[0].replaceAll("\\?", String.valueOf(m.getMigrationNumber())));
                            out.write(System.lineSeparator());
                        }

                        PreparedStatement st;
                        try {
                            if (out == null) {
                                String testVersionSql = helper.getSearchDatabaseVersionCommand();
                                st = new LoggablePreparedStatement(conn, testVersionSql);
                                st.setLong(1, m.getMigrationNumber());
                                log.info("Executing{}{}", System.lineSeparator(), st);
                                ResultSet rs = st.executeQuery();
                                if (!rs.next()) {
                                    throw new Exception("Migration " + m.getMigrationNumber() + " not found in table " + JFMigrationConstants.DB_VERSION_TABLE_NAME);
                                }
                            }

                            for (Change c : m.migration.getChanges()) {
                                for (Pair<String, Object[]> commands : c.getSqlCommand(helper)) {
                                    if (Data.class.isAssignableFrom(c.getClass())) {
                                        st = new LoggablePreparedStatement(conn, commands.getA());
                                        for (int i = 0; i < commands.getB().length; i++) {
                                            st.setObject(i + 1, commands.getB()[i]);
                                        }
                                        log.info("Executing{}{}", System.lineSeparator(), st);
                                        if (out == null) {
                                            st.executeUpdate();
                                        } else {
                                            out.write(st.toString().trim());
                                            out.write(System.lineSeparator());
                                            out.write(System.lineSeparator());
                                        }
                                    } else {
                                        st = new LoggablePreparedStatement(conn, commands.getA());
                                        log.info("Executing{}{}", System.lineSeparator(), st);
                                        if (out == null) {
                                            st.executeUpdate();
                                        } else {
                                            out.write(st.toString().trim());
                                            out.write(System.lineSeparator());
                                            out.write(System.lineSeparator());
                                        }
                                    }
                                }
                            }

                            String migrationVersionCommand = helper.getDeleteVersionCommand();
                            st = new LoggablePreparedStatement(conn, migrationVersionCommand);
                            st.setLong(1, m.getMigrationNumber());
                            log.info("Executing{}{}", System.lineSeparator(), st);
                            if (out == null) {
                                st.executeUpdate();
                            } else {
                                out.write(st.toString().trim());
                                out.write(System.lineSeparator());
                                out.write(System.lineSeparator());
                            }

                            if (out != null) {
                                if (scriptVersionCheck != null) {
                                    out.write(scriptVersionCheck[1]);
                                    out.write(System.lineSeparator());
                                }
                                out.write("--------------------------------------------");
                                out.write(System.lineSeparator());
                                out.write(System.lineSeparator());
                                out.write(System.lineSeparator());
                            }

                            log.debug("Applied migration {}", m.getClass().getSimpleName());
                        } catch (Exception e) {
                            if (out == null) {
                                conn.rollback(save);
                            }
                            throw e;
                        }
                    } else {
                        if (m.getMigrationNumber() > dbVersion) {
                            log.debug("Skipped migration {}({}) because out of range (db version: {})", m.getMigrationName(), m.getMigrationNumber(), dbVersion, targetMigration);
                        } else {
                            log.debug("Skipped migration {}({}) because out of range (target version: {})", m.getMigrationName(), m.getMigrationNumber(), targetMigration);
                        }
                    }
                }
            }

            if (out == null) {
                conn.commit();
            }
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    log.error("Connection rolled back");
                } catch (Exception ex) {
                    log.error("Error rolling back connection", ex);
                }
            }
            log.error("Error executing query", e);
            throw e;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                log.error(ex);
            }
        }
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}

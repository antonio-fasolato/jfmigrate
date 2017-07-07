package net.fasolato.jfmigrate;

import net.fasolato.jfmigrate.builders.Change;
import net.fasolato.jfmigrate.builders.Data;
import net.fasolato.jfmigrate.internal.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class JFMigrate {
    private static Logger log = LogManager.getLogger(JFMigrate.class);

    private List<String> packages;
    private SqlDialect dialect;

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
        IDialectHelper helper = getDialectHelper();
        DatabaseHelper dbHelper = new DatabaseHelper();

        Connection conn = null;
        try {
            conn = dbHelper.getConnection();
            conn.setAutoCommit(false);

            long dbVersion = getDatabaseVersion(helper, conn);
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
                    if (m.getMigrationNumber() > dbVersion) {
                        log.debug("Applying migration UP {}({})", m.getMigrationName(), m.getMigrationNumber());
                        m.up();

                        Savepoint save = conn.setSavepoint();
                        PreparedStatement st;
                        try {
                            for (Change c : m.migration.getChanges()) {
                                if (Data.class.isAssignableFrom(c.getClass())) {
                                    Data d = (Data) c;
                                    st = new LoggablePreparedStatement(conn, d.getSqlCommand(helper)[0]);
                                    for (int iv = 0; iv < d.getValues().length; iv++) {
                                        st.setObject(iv + 1, d.getValues()[iv]);
                                    }
                                    log.info("Executing{}{}", System.lineSeparator(), st);
                                    st.executeUpdate();
                                } else {
                                    for (String sql : c.getSqlCommand(helper)) {
                                        st = new LoggablePreparedStatement(conn, sql);
                                        log.info("Executing{}{}", System.lineSeparator(), st);
                                        st.executeUpdate();
                                    }
                                }
                            }

                            String migrationVersionCommand = helper.getInsertNewVersionCommand();
                            st = new LoggablePreparedStatement(conn, migrationVersionCommand);
                            st.setLong(1, m.getMigrationNumber());
                            st.setString(2, m.getMigrationName());
                            log.info("Executing{}{}", System.lineSeparator(), st);
                            st.executeUpdate();

                            log.debug("Applied migration {}", m.getClass().getSimpleName());
                        } catch (Exception e) {
                            conn.rollback(save);
                            throw e;
                        }
                    } else {
                        log.info("Skipping migration {} because DB is newer", m.getMigrationNumber());
                    }
                }
            }

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
                log.error("Connection rolled back");
            }
            log.error(e);
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
        IDialectHelper helper = getDialectHelper();
        DatabaseHelper dbHelper = new DatabaseHelper();

        Connection conn = null;
        try {
            conn = dbHelper.getConnection();
            conn.setAutoCommit(false);

            long dbVersion = getDatabaseVersion(helper, conn);
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

                        Savepoint save = conn.setSavepoint();
                        PreparedStatement st;
                        try {
                            String testVersionSql = helper.getSearchDatabaseVersionCommand();
                            st = new LoggablePreparedStatement(conn, testVersionSql);
                            st.setLong(1, m.getMigrationNumber());
                            log.info("Executing{}{}", System.lineSeparator(), st);
                            ResultSet rs = st.executeQuery();
                            if (!rs.next()) {
                                throw new Exception("Migration " + m.getMigrationNumber() + " not found in table " + JFMigrationConstants.DB_VERSION_TABLE_NAME);
                            }

                            for (Change c : m.migration.getChanges()) {
                                for (String sql : c.getSqlCommand(helper)) {
                                    st = new LoggablePreparedStatement(conn, sql);
                                    log.info("Executing{}{}", System.lineSeparator(), st);
                                    st.executeUpdate();
                                }
                            }

                            String migrationVersionCommand = helper.getDeleteVersionCommand();
                            st = new LoggablePreparedStatement(conn, migrationVersionCommand);
                            st.setLong(1, m.getMigrationNumber());
                            log.info("Executing{}{}", System.lineSeparator(), st);
                            st.executeUpdate();

                            log.debug("Applied migration {}", m.getClass().getSimpleName());
                        } catch (Exception e) {
                            conn.rollback(save);
                            throw e;
                        }
                    } else {
                        log.debug("Skipped migration {}({}) because out of range (db version: {}, target migration: {})", m.getMigrationName(), m.getMigrationNumber(), dbVersion, targetMigration);
                    }
                }
            }

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
                log.error("Connection rolled back");
            }
            log.error(e);
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
}

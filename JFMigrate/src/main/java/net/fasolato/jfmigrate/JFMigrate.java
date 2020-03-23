package net.fasolato.jfmigrate;

import net.fasolato.jfmigrate.builders.Change;
import net.fasolato.jfmigrate.builders.Data;
import net.fasolato.jfmigrate.builders.RawSql;
import net.fasolato.jfmigrate.internal.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Main class to manage JFMigrate
 */
public class JFMigrate {
    private static Logger log = LogManager.getLogger(JFMigrate.class);

    private static final String DEFAULT_SCRIPT_SEPARATOR = ";";

    private List<String> packages;
    private SqlDialect dialect;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String schema;
    private String scriptSeparator;

    /**
     * Constructor that bootstraps JFMigrate.
     *
     * It basically reads a jfmigrate.properties file in the classpath and configures the library (database dialcet, connection string...)
     */
    @Deprecated
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
            } else if (configDialect.equalsIgnoreCase("oracle")) {
                dialect = SqlDialect.ORACLE;
            } else if (configDialect.equalsIgnoreCase("sqlite")) {
                dialect = SqlDialect.SQLITE;
            }

            String scriptLineSeparator = DEFAULT_SCRIPT_SEPARATOR;
            if(properties.getProperty("jfmigrate.db.script_line_separator") != null) {
                scriptLineSeparator = properties.getProperty("jfmigrate.db.script_line_separator");
            }

            String configUrl = properties.getProperty("jfmigrate.db.url");
            String configUsername = properties.getProperty("jfmigrate.db.username");
            String configPassword = properties.getProperty("jfmigrate.db.password");
            String configDriverClassName = properties.getProperty("jfmigrate.db.driverClassName");

            init(dialect, configUrl, configUsername, configPassword, configDriverClassName, scriptLineSeparator);
        } catch (IOException e) {
            log.error(e);
            throw new JFException("Error reading properties file", e);
        }
    }

    public JFMigrate(SqlDialect dialect, String url, String username, String password, String driverClassName) {
        init(dialect, url, username, password, driverClassName, DEFAULT_SCRIPT_SEPARATOR);
    }

    public JFMigrate(SqlDialect dialect, String url, String username, String password, String driverClassName, String scriptLineSeparator) {
        init(dialect, url, username, password, driverClassName, scriptLineSeparator);
    }

    private void init(SqlDialect dialect, String url, String username, String password, String driverClassName, String scriptLineSeparator) {
        this.dialect = dialect;
        this.scriptSeparator = scriptLineSeparator;
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
        packages = new ArrayList<>();
    }

    /**
     * Registers a package by name as a source of migration classes
     * @param pkg The package name
     */
    public void registerPackage(String... pkg) {
        for(String p: pkg) {
            packages.add(p);
        }
    }

    /**
     * Registers a package from a class object as a source of migration classes.
     * @param clazz The class belonging to the package to use as a source
     */
    public void registerPackage(Class<?>... clazz) {
        for(Class<?> c : clazz) {
            packages.add(c.getPackage().getName());
        }
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
            case ORACLE:
                return new OracleDialectHelper();
            case SQLITE:
                return new SqliteDialectHelper();
            default:
                throw new NotImplementedException();
        }
    }

    private long getDatabaseVersion(IDialectHelper helper, Connection conn) throws SQLException {
        String versionTableExistence = helper.getDatabaseVersionTableExistenceCommand();
        boolean exists = true;
        ResultSet rs = null;
        PreparedStatement st = new LoggablePreparedStatement(conn, versionTableExistence);
        log.info("Executing{}{}", System.lineSeparator(), st);
        try {
            rs = st.executeQuery();
            if (!rs.next()) {
                exists = false;
            } else {
                if (rs.getInt(1) == 0) {
                    exists = false;
                }
            }
        } catch (SQLSyntaxErrorException oracleException) {
            if (oracleException.getMessage().startsWith("ORA-00942:")) {
                exists = false;
            } else {
                throw oracleException;
            }
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                    st.close();
                }
            } catch(Exception ex) {
                log.error("Error closing resultset/ststement", ex);
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
        rs.close();
        st.close();
        return dbVersion;
    }

    private void createVersionTable(IDialectHelper helper, Connection conn) throws SQLException {
        String createCommand = helper.getVersionTableCreationCommand();

        PreparedStatement st = new LoggablePreparedStatement(conn, createCommand);
        log.info("Executing{}{}", System.lineSeparator(), st);
        st.execute();
    }

    /**
     * Method to start an UP migration running against a real database engine.
     * @throws Exception
     */
    public void migrateUp() throws Exception {
        migrateUp(-1, null, false);
    }

    /**
     * Method to start an UP migration with a Writer output (to write for example an output file).
     * @param out The Writer to write the SQL code to
     * @throws Exception
     */
    public void migrateUp(Writer out) throws Exception {
        migrateUp(-1, out, false);
    }

    /**
     * Method to start an UP migration with a Writer output (to write for example an output file).
     * @param out The Writer to write the SQL code to
     * @param createVersionInfoTable Flag to decide whether to create the migration history table if missing
     * @throws Exception
     */
    public void migrateUp(Writer out, boolean createVersionInfoTable) throws Exception {
        migrateUp(-1, out, createVersionInfoTable);
    }

    /**
     * Method to start an UP migration with a Writer output (to write for example an output file).
     * @param startMigrationNumber Force JFMigrate to start from this migration (the existence of this migration is tested anyway)
     * @param out The Writer to write the SQL code to
     * @param createVersionInfoTable Flag to decide whether to create the migration history table if missing
     * @throws Exception
     */
    public void migrateUp(int startMigrationNumber, Writer out, boolean createVersionInfoTable) throws Exception {
        IDialectHelper helper = getDialectHelper();
        DatabaseHelper dbHelper = new DatabaseHelper(this.dialect.toString(), this.url, this.username, this.password, this.driverClassName);

        String rowSeparator = "";
        if(dialect == SqlDialect.ORACLE && out != null) {
            rowSeparator = ";";
        }

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
                out.write(helper.getVersionTableCreationCommand() + rowSeparator);
                out.write(System.lineSeparator());
                out.write(System.lineSeparator());
                out.write("--------------------------------------------");
                out.write(System.lineSeparator());
                out.write(System.lineSeparator());
                out.write(System.lineSeparator());
                out.flush();
            }
            log.info("Current database version: {}", dbVersion);

            for (String p : packages) {
                log.debug("Migrating up from package {}", p);

                List<JFMigrationClass> migrations = ReflectionHelper.getAllMigrations(p);
                Collections.sort(migrations, Comparator.comparingLong(JFMigrationClass::getMigrationNumber));

                for (JFMigrationClass m : migrations) {
                    if (m.executeForDialect(dialect) && (m.getMigrationNumber() > dbVersion && (startMigrationNumber == -1 || m.getMigrationNumber() >= startMigrationNumber))) {
                        log.debug("Applying migration UP {}({})", m.getMigrationName(), m.getMigrationNumber());
                        m.up();

                        String[] scriptVersionCheck = null;
                        if (out != null) {
                            out.write(String.format("-- Migration %s(%s)", m.getMigrationName(), m.getMigrationNumber()));
                            out.write(System.lineSeparator());
                            out.write(System.lineSeparator());

                            scriptVersionCheck = helper.getScriptCheckMigrationUpVersionCommand();
                            if (scriptVersionCheck != null && scriptVersionCheck.length != 0) {
                                out.write(scriptVersionCheck[0].replaceAll("\\?", String.valueOf(m.getMigrationNumber())) + rowSeparator);
                                out.write(System.lineSeparator());
                            }
                            out.flush();
                        }
                        PreparedStatement st;
                        for (Change c : m.migration.getChanges()) {
                            if (Data.class.isAssignableFrom(c.getClass())) {
                                Data d = (Data) c;

                                for (Pair<String, Object[]> commands : d.getSqlCommand(helper)) {
                                    st = new LoggablePreparedStatement(conn, commands.getLeft());
                                    for (int iv = 0; iv < commands.getRight().length; iv++) {
                                        Object value = commands.getRight()[iv];
                                        if(dialect == SqlDialect.ORACLE && value instanceof Date) {
                                            value = new java.sql.Date(((Date) value).getTime());
                                        }
                                        st.setObject(iv + 1, value);
                                    }
                                    log.info("Executing{}{}", System.lineSeparator(), st);
                                    if (out == null) {
                                        st.execute();
                                    } else {
                                        out.write(st.toString().trim() + rowSeparator);
                                        out.write(System.lineSeparator());
                                        out.write(System.lineSeparator());
                                        out.flush();
                                    }
                                }
                            } else if(RawSql.class.isAssignableFrom(c.getClass())) {
                                RawSql raw = (RawSql) c;
                                if(((RawSql) c).isScript()) {
                                    for (Pair<String, Object[]> command : c.getSqlCommand(helper)) {
                                        String script = command.getLeft();

                                        List<String> rows = ScriptParser.parseScript(script, scriptSeparator);
                                        for(String row : rows) {
                                            st = new LoggablePreparedStatement(conn, row);
                                            log.info("Executing{}{}", System.lineSeparator(), st);
                                            if (out == null) {
                                                st.execute();
                                                st.close();
                                            } else {
                                                out.write(st.toString().trim() + rowSeparator);
                                                out.write(System.lineSeparator());
                                                out.write(System.lineSeparator());
                                                out.flush();
                                            }
                                        }
                                    }
                                } else {
                                    for (Pair<String, Object[]> commands : c.getSqlCommand(helper)) {
                                        st = new LoggablePreparedStatement(conn, commands.getLeft());
                                        log.info("Executing{}{}", System.lineSeparator(), st);
                                        if (out == null) {
                                            st.execute();
                                            st.close();
                                        } else {
                                            out.write(st.toString().trim() + rowSeparator);
                                            out.write(System.lineSeparator());
                                            out.write(System.lineSeparator());
                                            out.flush();
                                        }
                                    }
                                }
                            } else {
                                for (Pair<String, Object[]> commands : c.getSqlCommand(helper)) {
                                    st = new LoggablePreparedStatement(conn, commands.getLeft());
                                    if (commands.getRight() != null) {
                                        for (int iv = 0; iv < commands.getRight().length; iv++) {
                                            Object value = commands.getRight()[iv];
                                            if(dialect == SqlDialect.ORACLE && value instanceof Date) {
                                                value = new java.sql.Date(((Date) value).getTime());
                                            }
                                            st.setObject(iv + 1, value);
                                        }
                                    }
                                    log.info("Executing{}{}", System.lineSeparator(), st);
                                    if (out == null) {
                                        st.execute();
                                    } else {
                                        out.write(st.toString().trim() + rowSeparator);
                                        out.write(System.lineSeparator());
                                        out.write(System.lineSeparator());
                                        out.flush();
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
                            st.execute();
                        } else {
                            out.write(st.toString().trim() + rowSeparator);
                            out.write(System.lineSeparator());
                            out.write(System.lineSeparator());
                            out.flush();
                        }

                        if (out != null) {
                            if (scriptVersionCheck != null) {
                                out.write(scriptVersionCheck[1] + rowSeparator);
                                out.write(System.lineSeparator());
                            }
                            out.write("--------------------------------------------");
                            out.write(System.lineSeparator());
                            out.write(System.lineSeparator());
                            out.write(System.lineSeparator());
                            out.flush();
                        }
                        log.debug("Applied migration {}", m.getClass().getSimpleName());
                    } else {
                        if(!m.executeForDialect(dialect)) {
                            log.info("Skipping migration {} because DB dialect {} is explicitly skipped", m.getMigrationNumber(), dialect);
                        } else if (m.getMigrationNumber() <= dbVersion) {
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

    /**
     * Method to start an DOWN migration running against a true database engine. JFMigrate starts from the current DB migration and executes DOWN migrations until it reaches targetMigration.
     * @param targetMigration The migration number where to stop. The initial database state is migration 0.
     * @throws Exception
     */
    public void migrateDown(int targetMigration) throws Exception {
        migrateDown(targetMigration, null);
    }

    /**
     * Method to start an DOWN migration with a Writer output (to write for example an output file). JFMigrate starts from the current DB migration and executes DOWN migrations until it reaches targetMigration.
     * @param targetMigration The migration number where to stop. The initial database state is migration 0.
     * @param out The Writer to write the SQL code to
     * @throws Exception
     */
    public void migrateDown(int targetMigration, Writer out) throws Exception {
        IDialectHelper helper = getDialectHelper();
        DatabaseHelper dbHelper = new DatabaseHelper(this.dialect.toString(), this.url, this.username, this.password, this.driverClassName);

        String rowSeparator = "";
        if(dialect == SqlDialect.ORACLE && out != null) {
            rowSeparator = ";";
        }

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
                    if (m.executeForDialect(dialect) && (m.getMigrationNumber() <= dbVersion && m.getMigrationNumber() > targetMigration)) {
                        log.debug("Applying migration DOWN {}({})", m.getMigrationName(), m.getMigrationNumber());
                        m.down();

                        String[] scriptVersionCheck = null;
                        if (out != null) {
                            out.write(String.format("-- Migration down %s(%s)", m.getMigrationName(), m.getMigrationNumber()));
                            out.write(System.lineSeparator());
                            out.write(System.lineSeparator());
                            out.flush();
                        }

                        scriptVersionCheck = helper.getScriptCheckMigrationDownVersionCommand();
                        if (out != null && scriptVersionCheck != null) {
                            out.write(scriptVersionCheck[0].replaceAll("\\?", String.valueOf(m.getMigrationNumber())) + rowSeparator);
                            out.write(System.lineSeparator());
                            out.flush();
                        }

                        PreparedStatement st;
                        if (out == null) {
                            String testVersionSql = helper.getSearchDatabaseVersionCommand();
                            st = new LoggablePreparedStatement(conn, testVersionSql);
                            st.setLong(1, m.getMigrationNumber());
                            log.info("Executing{}{}", System.lineSeparator(), st);
                            ResultSet rs = st.executeQuery();
                            if (!rs.next()) {
                                throw new Exception("Migration " + m.getMigrationNumber() + " not found in table " + JFMigrationConstants.DB_VERSION_TABLE_NAME);
                            }
                            rs.close();
                            st.close();
                        }

                        for (Change c : m.migration.getChanges()) {
                            for (Pair<String, Object[]> commands : c.getSqlCommand(helper)) {
                                if (Data.class.isAssignableFrom(c.getClass())) {
                                    st = new LoggablePreparedStatement(conn, commands.getLeft());
                                    if (commands.getRight() != null) {
                                        for (int i = 0; i < commands.getRight().length; i++) {
                                            Object value = commands.getRight()[i];
                                            if(dialect == SqlDialect.ORACLE && value instanceof Date) {
                                                value = new java.sql.Date(((Date) value).getTime());
                                            }
                                            st.setObject(i + 1, value);
                                        }
                                    }
                                    log.info("Executing{}{}", System.lineSeparator(), st);
                                    if (out == null) {
                                        st.execute();
                                    } else {
                                        out.write(st.toString().trim() + rowSeparator);
                                        out.write(System.lineSeparator());
                                        out.write(System.lineSeparator());
                                        out.flush();
                                    }
                                } else if(RawSql.class.isAssignableFrom(c.getClass())) {
                                    RawSql raw = (RawSql) c;
                                    if(((RawSql) c).isScript()) {
                                        String script = commands.getLeft();

                                        List<String> rows = ScriptParser.parseScript(script, scriptSeparator);
                                        for(String row : rows) {
                                            st = new LoggablePreparedStatement(conn, row);
                                            log.info("Executing{}{}", System.lineSeparator(), st);
                                            if (out == null) {
                                                st.execute();
                                                st.close();
                                            } else {
                                                out.write(st.toString().trim() + rowSeparator);
                                                out.write(System.lineSeparator());
                                                out.write(System.lineSeparator());
                                                out.flush();
                                            }
                                        }
                                    } else {
                                        st = new LoggablePreparedStatement(conn, commands.getLeft());
                                        log.info("Executing{}{}", System.lineSeparator(), st);
                                        if (out == null) {
                                            st.execute();
                                            st.close();
                                        } else {
                                            out.write(st.toString().trim() + rowSeparator);
                                            out.write(System.lineSeparator());
                                            out.write(System.lineSeparator());
                                            out.flush();
                                        }
                                    }
                                } else {
                                    st = new LoggablePreparedStatement(conn, commands.getLeft());
                                    log.info("Executing{}{}", System.lineSeparator(), st);
                                    if (out == null) {
                                        st.execute();
                                    } else {
                                        out.write(st.toString().trim() + rowSeparator);
                                        out.write(System.lineSeparator());
                                        out.write(System.lineSeparator());
                                        out.flush();
                                    }
                                }
                            }
                        }

                        String migrationVersionCommand = helper.getDeleteVersionCommand();
                        st = new LoggablePreparedStatement(conn, migrationVersionCommand);
                        st.setLong(1, m.getMigrationNumber());
                        log.info("Executing{}{}", System.lineSeparator(), st);
                        if (out == null) {
                            st.execute();
                        } else {
                            out.write(st.toString().trim() + rowSeparator);
                            out.write(System.lineSeparator());
                            out.write(System.lineSeparator());
                            out.flush();
                        }

                        if (out != null) {
                            if (scriptVersionCheck != null) {
                                out.write(scriptVersionCheck[1] + rowSeparator);
                                out.write(System.lineSeparator());
                            }
                            out.write("--------------------------------------------");
                            out.write(System.lineSeparator());
                            out.write(System.lineSeparator());
                            out.write(System.lineSeparator());
                            out.flush();
                        }

                        log.debug("Applied migration {}", m.getClass().getSimpleName());
                    } else {
                        if(!m.executeForDialect(dialect)) {
                            log.info("Skipping migration {} because DB dialect {} is explicitly skipped", m.getMigrationNumber(), dialect);
                        } else if (m.getMigrationNumber() > dbVersion) {
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

    /**
     * Retrieves the current schema, if set
     * @return
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Sets the database schema to use (if applicable)
     * @param schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }
}

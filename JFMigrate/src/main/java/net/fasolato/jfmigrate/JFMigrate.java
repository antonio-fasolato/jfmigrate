package net.fasolato.jfmigrate;

import net.fasolato.jfmigrate.builders.Change;
import net.fasolato.jfmigrate.internal.DatabaseHelper;
import net.fasolato.jfmigrate.internal.IDialectHelper;
import net.fasolato.jfmigrate.internal.ReflectionHelper;
import net.fasolato.jfmigrate.internal.SqlServerDialectHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 20/03/2017.
 */
public class JFMigrate {
    private static Logger log = LogManager.getLogger(JFMigrate.class);

    private List<String> packages;
    private SqlDialect dialect;

    public JFMigrate(SqlDialect dialect) {
        this.dialect = dialect;
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
            default:
                throw new NotImplementedException();
        }
    }

    private int getDatabaseVersion(IDialectHelper helper, Connection conn) throws SQLException {
        String currentVersionCommand = helper.getDatabaseVersionCommand();

        log.info("Executing {}", currentVersionCommand);
        PreparedStatement st = conn.prepareStatement(currentVersionCommand);
        ResultSet rs = st.executeQuery();
        int dbVersion = -1;
        if (rs.next()) {
            dbVersion = rs.getInt(1);
        }
        return dbVersion;
    }

    private void createVersionTable(IDialectHelper helper, Connection conn) throws SQLException {
        String createCommand = helper.getVersionTableCreationCommand();

        log.info("Executing {}", createCommand);
        PreparedStatement st = conn.prepareStatement(createCommand);
        st.executeUpdate();

    }

    public void migrateUp() throws Exception {
        IDialectHelper helper = getDialectHelper();
        DatabaseHelper dbHelper = new DatabaseHelper();

        Connection conn = null;
        try {
            conn = dbHelper.getConnection();
            conn.setAutoCommit(false);

            int dbVersion = getDatabaseVersion(helper, conn);
            log.info("Current database version: {}", dbVersion);

            if (dbVersion == -1) {
                //No migration table, it must be created
                createVersionTable(helper, conn);
            }

            for (String p : packages) {
                log.debug("Migrating up from package {}", p);
                for (JFMigrationClass m : ReflectionHelper.getAllMigrations(p)) {
                    if (m.getMigrationNumber() > dbVersion) {
                        log.debug("Applying migration {}({})", m.getMigrationName(), m.getMigrationNumber());
                        m.up();

                        Savepoint save = conn.setSavepoint();
                        PreparedStatement st;
                        try {
                            for (Change c : m.migration.getChanges()) {
                                for (String sql : c.getSqlCommand(helper)) {
                                    log.info("Executing: {}", sql);
                                    st = conn.prepareStatement(sql);
                                    st.executeUpdate();
                                }
                            }

                            String migrationVersionCommand = helper.getInsertNewVersionCommand();
                            st = conn.prepareStatement(migrationVersionCommand);
                            st.setInt(1, m.getMigrationNumber());
                            st.setString(2, m.getMigrationName());
                            st.executeUpdate();

                            log.debug("Applied migration {}", m.getClass().getSimpleName());
                        } catch (Exception e) {
                            conn.rollback(save);
                            log.error(e);
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

    public void migrateDown() throws Exception {
        IDialectHelper helper = getDialectHelper();
        DatabaseHelper dbHelper = new DatabaseHelper();

        Connection conn = null;
        try {
            conn = dbHelper.getConnection();
            conn.setAutoCommit(false);

            int dbVersion = getDatabaseVersion(helper, conn);
            log.info("Current database version: {}", dbVersion);

            if (dbVersion <= 0) {
                //No migration table or DB is at first migration, nothing to be done
                return;
            }

            for (String p : packages) {
                log.debug("Migrating down from package {}", p);
                for (JFMigrationClass m : ReflectionHelper.getAllMigrations(p)) {
                    log.debug("Applying migration {}", m.getClass().getSimpleName());
                    m.down();

//                for (Table t : m.getDatabase().getRemovedTables()) {
//                    String command = helper.tableDropping(m.getDatabase().getDatabaseName(), m.getDatabase().getSchemaName(), t);
//                    log.debug(command);
//                }

                    log.debug("Applied migration {}", m.getClass().getSimpleName());
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

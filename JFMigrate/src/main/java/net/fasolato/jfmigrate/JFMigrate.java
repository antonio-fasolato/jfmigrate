package net.fasolato.jfmigrate;

import net.fasolato.jfmigrate.builders.Column;
import net.fasolato.jfmigrate.builders.Table;
import net.fasolato.jfmigrate.internal.IDialectHelper;
import net.fasolato.jfmigrate.internal.ReflectionHelper;
import net.fasolato.jfmigrate.internal.SqlServerDialectHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    public void migrateUp() throws Exception {
        IDialectHelper helper = getDialectHelper();

        for (String p : packages) {
            log.debug("Migrating up from package {}", p);
            for (IMigration m : ReflectionHelper.getAllMigrations(p)) {
                log.debug("Applying migration {}", m.getClass().getSimpleName());
                m.up();

                for (Table t : m.getDatabase().getNewTables()) {
                    String command = helper.tableCreation(m.getDatabase().getDatabaseName(), m.getDatabase().getSchemaName(), t);
                    log.debug(command);
                }

                log.debug("Applied migration {}", m.getClass().getSimpleName());
            }
        }
    }

    public void migrateDown() throws Exception {
        IDialectHelper helper = getDialectHelper();

        for (String p : packages) {
            log.debug("Migrating down from package {}", p);
            for (IMigration m : ReflectionHelper.getAllMigrations(p)) {
                log.debug("Applying migration {}", m.getClass().getSimpleName());
                m.down();

                for (Table t : m.getDatabase().getRemovedTables()) {
                    String command = helper.tableDropping(m.getDatabase().getDatabaseName(), m.getDatabase().getSchemaName(), t);
                    log.debug(command);
                }

                log.debug("Applied migration {}", m.getClass().getSimpleName());
            }
        }
    }
}

package net.fasolato.jfmigrate;

import net.fasolato.jfmigrate.builders.Column;
import net.fasolato.jfmigrate.builders.Table;
import net.fasolato.jfmigrate.internal.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 20/03/2017.
 */
public class JFMigrate {
    private static Logger log = LogManager.getLogger(JFMigrate.class);

    private List<String> packages;

    public JFMigrate() {
        packages = new ArrayList<String>();
    }

    public void registerPackage(String pkg) {
        packages.add(pkg);
    }

    public void registerPackage(Class<?> clazz) {
        packages.add(clazz.getPackage().getName());
    }

    public void migrateUp() throws Exception {
        for (String p : packages) {
            log.debug("Migrating up from package {}", p);
            for (IMigration m : ReflectionHelper.getAllMigrations(p)) {
                log.debug("Applying migration {}", m.getClass().getSimpleName());
                m.up();

                for (Table t : m.getDatabase().getNewTables()) {
                    log.debug("Create table {}", t.getName());
                    for (Column c : t.getColumns()) {
                        log.debug("   Column: {} {}", c.getName(), c.getType());
                    }
                }

                log.debug("Applied migration {}", m.getClass().getSimpleName());
            }
        }
    }

    public void migrateDown() throws Exception {
        for (String p : packages) {
            log.debug("Migrating down from package {}", p);
            for (IMigration m : ReflectionHelper.getAllMigrations(p)) {
                log.debug("Applying migration {}", m.getClass().getSimpleName());
                m.down();

                for (Table t : m.getDatabase().getRemovedTables()) {
                    log.debug("Drop table {}", t.getName());
                }

                log.debug("Applied migration {}", m.getClass().getSimpleName());
            }
        }
    }
}

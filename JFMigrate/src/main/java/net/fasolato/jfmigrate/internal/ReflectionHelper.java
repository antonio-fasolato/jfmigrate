package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.JFMigrationClass;
import net.fasolato.jfmigrate.Migration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.util.*;

/**
 * Created by fasolato on 16/03/2017.
 */
public class ReflectionHelper {
    private static Logger log = LogManager.getLogger(ReflectionHelper.class);

    public static List<JFMigrationClass> getAllMigrations(String pkg) throws Exception {
        if (pkg == null || pkg.trim().length() == 0) {
            throw new Exception("Null or empty package passed");
        }
        Reflections reflections = new Reflections(pkg);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Migration.class);
        List<JFMigrationClass> migrations = new ArrayList<JFMigrationClass>();
        for (Class<?> c : classes) {
            if (!JFMigrationClass.class.isAssignableFrom(c)) {
                log.debug("class {} does not implement IMigration. Class is ignored", c.getSimpleName());
            } else {
                log.debug("class {} implements IMigration. Class is added as a migration", c.getSimpleName());
                migrations.add((JFMigrationClass) c.newInstance());
            }
        }

        return migrations;
    }
}

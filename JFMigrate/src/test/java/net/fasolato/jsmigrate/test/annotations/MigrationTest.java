package net.fasolato.jsmigrate.test.annotations;

import net.fasolato.jfmigrate.IMigration;
import net.fasolato.jfmigrate.Migration;
import org.junit.Test;
import org.reflections.Reflections;

import static org.junit.Assert.*;

import java.util.Set;

/**
 * Created by fasolato on 16/03/2017.
 */
public class MigrationTest {
    @Test
    public void getSingleMigration() {
        String pkg = "net.fasolato.jsmigrate.test.annotations.migrations.single";
        Reflections reflections = new Reflections(pkg);

        Set<Class<?>> migrations = reflections.getTypesAnnotatedWith(Migration.class);

        assertEquals("Migration classes from package " + pkg + " should be 1", 1, migrations.size());
        for (Class<?> m : migrations) {
            assertEquals("The migration from package " + pkg + "should be named M001SingleMigration", "M001SingleMigration", m.getSimpleName());
            assertEquals("The migration from package " + pkg + "should implement IMigration", true, IMigration.class.isAssignableFrom(m));
        }
    }
}

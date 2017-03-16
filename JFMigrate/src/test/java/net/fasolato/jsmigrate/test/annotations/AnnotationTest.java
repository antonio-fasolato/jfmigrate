package net.fasolato.jsmigrate.test.annotations;

import net.fasolato.jfmigrate.IMigration;
import net.fasolato.jfmigrate.Migration;
import org.junit.Test;
import org.reflections.Reflections;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Created by fasolato on 16/03/2017.
 */
public class AnnotationTest {
    @Test
    public void getSingleMigration() {
        String pkg = "net.fasolato.jsmigrate.test.annotations.migrations.single";
        Reflections reflections = new Reflections(pkg);

        Set<Class<?>> migrations = reflections.getTypesAnnotatedWith(Migration.class);

        assertEquals("Migration classes from package " + pkg + " should be 1", 1, migrations.size());
        for (Class<?> m : migrations) {
            assertEquals("The migration from package " + pkg + " should be named M001SingleMigration", "M001SingleMigration", m.getSimpleName());
            assertEquals("The migration from package " + pkg + " should implement IMigration", true, IMigration.class.isAssignableFrom(m));
            Annotation[] annotations = m.getDeclaredAnnotations();
            assertEquals("The migration from package " + pkg + " should have a single custom annotation", 1, annotations.length);
            assertEquals("The annotation of the migration from package " + pkg + "should be Migration", true, Migration.class.isAssignableFrom(annotations[0].getClass()));
            Migration annotation = (Migration)annotations[0];
            assertEquals("The annotation of the migration from package " + pkg + " should have a number value of 1", 1, annotation.number());
            assertEquals("The annotation of the migration from package " + pkg + " should have a description value of Single migration", "Single migration", annotation.description());
        }
    }
}

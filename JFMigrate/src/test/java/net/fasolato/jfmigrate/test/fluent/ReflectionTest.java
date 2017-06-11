package net.fasolato.jfmigrate.test.fluent;

import net.fasolato.jfmigrate.JFMigrationClass;
import net.fasolato.jfmigrate.internal.ReflectionHelper;
import net.fasolato.jfmigrate.test.fluent.migrations.M01BaseTable;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.*;

public class ReflectionTest {
    @Test
    public void testListClassesInPackage() {
        String pkg = M01BaseTable.class.getPackage().getName();
        try {
            List<String> classes = ReflectionHelper.listClassesInPackage(pkg);
            assertEquals(1, classes.size());

            List<JFMigrationClass> migrations = ReflectionHelper.getAllMigrations(pkg);
            assertEquals(1, migrations.size());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

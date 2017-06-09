package net.fasolato.jfmigrate.test.fluent;

import net.fasolato.jfmigrate.JFMigrationFluent;
import net.fasolato.jfmigrate.builders.Column;
import net.fasolato.jfmigrate.builders.Index;
import net.fasolato.jfmigrate.builders.Table;
import net.fasolato.jfmigrate.test.fluent.migrations.M01BaseTable;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.JDBCType;

import static org.junit.Assert.*;

/**
 * Created by fasolato on 06/04/2017.
 */
public class FluentInterfaceTest {
    @Test
    public void testSimpleTable() {
        M01BaseTable m01 = new M01BaseTable();
        m01.up();

        try {
            Field migrationField = m01.getClass().getSuperclass().getDeclaredField("migration");
            assertNotNull("Migration field is not null", migrationField);
            migrationField.setAccessible(true);
            Object oMigration = migrationField.get(m01);
            assertNotNull("Migration value is not null", oMigration);
            System.out.println(oMigration.getClass().getName());
            assertTrue("m01 is a JFMigrationFluent object", JFMigrationFluent.class.isAssignableFrom(oMigration.getClass()));
            JFMigrationFluent migration = (JFMigrationFluent) oMigration;

//            assertNotNull("Added tables must be initialized", migration.getAddedTables());
//            assertEquals("There must be one new table", 1, migration.getAddedTables().size());
//            Table t = migration.getAddedTables().get(0);
//            assertEquals("Table name must be M01Table", "M01Table", t.getName());
//            assertNotNull("Table M01Table must have added columns initialized", t.getAddedColumns());
//            assertEquals("Table M01Table must have 2 columns", 2, t.getAddedColumns().size());
//
//            Column c1 = t.getAddedColumns().get(0);
//            Column c2 = t.getAddedColumns().get(1);
//            assertEquals("Column 1 must have name id", "id", c1.getName());
//            assertEquals("Column 1 must have type int", JDBCType.INTEGER, c1.getType());
//            assertTrue("Column 1 must be a primary key", c1.isPrimaryKey());
//            assertTrue("Column 1 must be an identity", c1.isIdentity());
//            assertEquals("Column 2 must have name description", "description", c2.getName());
//            assertEquals("Column 2 must have type string", JDBCType.VARCHAR, c2.getType());
//
//            assertNotNull("Migration must have createdIndexes initialized", migration.getCreatedIndexes());
//            assertEquals("Migration must have 1 index", 1, migration.getCreatedIndexes().size());
//            Table idx = migration.getCreatedIndexes().get(0);
//            assertEquals("The index must be on table M01Table", "M01Table", idx.getName());

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            fail("migration object does not exist");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            fail("migration object cannot be accessed");
        }
    }
}

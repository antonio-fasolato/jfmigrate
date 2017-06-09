package net.fasolato.jfmigrate.test.fluent.migrations;

import net.fasolato.jfmigrate.JFMigrationClass;
import net.fasolato.jfmigrate.Migration;

/**
 * Created by fasolato on 06/04/2017.
 */
@Migration(number = 1)
public class M01BaseTable extends JFMigrationClass {
    public void up() {
        migration.createTable("M01Table")
                .withColumn("id").asInteger().primaryKey().identity()
                .withColumn("description").asString();

        migration.createIndex("M01Table").withIndexedColumn("description");
    }

    public void down() {
        migration.deleteTable("Mo1Table");
    }
}

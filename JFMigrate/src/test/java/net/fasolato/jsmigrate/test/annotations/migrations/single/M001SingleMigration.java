package net.fasolato.jsmigrate.test.annotations.migrations.single;

import net.fasolato.jfmigrate.JFMigration;
import net.fasolato.jfmigrate.Migration;

/**
 * Created by fasolato on 16/03/2017.
 */
@Migration(number = 1, description = "Single migration")
public class M001SingleMigration extends JFMigration {
    public void up() {

    }

    public void down() {

    }
}

package net.fasolato.jfmigrate;

import net.fasolato.jfmigrate.builders.Database;

/**
 * Created by fasolato on 20/03/2017.
 */
public abstract class JFMigration implements IMigration {
    protected Database database;

    public JFMigration() {
        database = new Database();
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }
}

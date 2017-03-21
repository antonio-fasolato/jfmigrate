package net.fasolato.jfmigrate;

import net.fasolato.jfmigrate.builders.Database;

/**
 * Created by fasolato on 16/03/2017.
 */
public interface IMigration {
    void up();

    void down();

    Database getDatabase();

    void setDatabase(Database database);

}

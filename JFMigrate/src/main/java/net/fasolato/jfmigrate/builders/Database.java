package net.fasolato.jfmigrate.builders;

/**
 * Created by fasolato on 20/03/2017.
 */
public class Database {
    public Table createTable(String name) {
        return new Table(name);
    }
}

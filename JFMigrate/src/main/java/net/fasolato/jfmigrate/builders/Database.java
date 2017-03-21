package net.fasolato.jfmigrate.builders;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 20/03/2017.
 */
public class Database {
    private List<Table> newTables;
    private List<Table> removedTables;

    public Database() {
        newTables = new ArrayList<Table>();
        removedTables = new ArrayList<Table>();
    }

    public Table createTable(String name) {
        Table t = new Table(name);
        newTables.add(t);
        return t;
    }

    public void dropTable(String name) {
        Table t = new Table(name);
        removedTables.add(t);
    }

    public List<Table> getNewTables() {
        return newTables;
    }

    public List<Table> getRemovedTables() {
        return removedTables;
    }
}

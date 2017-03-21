package net.fasolato.jfmigrate.builders;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 20/03/2017.
 */
public class Database {
    private List<Table> newTables;
    private List<Table> removedTables;
    private String databaseName;
    private String schemaName;

    public Database() {
        newTables = new ArrayList<Table>();
        removedTables = new ArrayList<Table>();
    }

    public Database databaseName(String name) {
        databaseName = name;
        return this;
    }

    public Database schemaName(String name) {
        schemaName = name;
        return this;
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

    public String getDatabaseName() {
        return databaseName;
    }

    public String getSchemaName() {
        return schemaName;
    }
}

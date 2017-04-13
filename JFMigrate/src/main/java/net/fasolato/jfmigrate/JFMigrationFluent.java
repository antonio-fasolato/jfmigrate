package net.fasolato.jfmigrate;

import net.fasolato.jfmigrate.builders.Column;
import net.fasolato.jfmigrate.builders.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 04/04/2017.
 */
public class JFMigrationFluent {
    private List<Table> addedTables;
    private List<Table> alteredTables;
    private List<Table> renamedTables;
    private List<Column> renamedColumns;
    private List<Table> createdIndexes;
    private List<Table> deletedTables;
    private List<Column> deletedColumns;
    private List<String> commands;
    private List<String> scripts;

    public Table createTable(String tableName) {
        if (addedTables == null) {
            addedTables = new ArrayList<Table>();
        }

        Table t = new Table(tableName);
        addedTables.add(t);
        return t;
    }

    public Table alterTable(String tableName) {
        if (alteredTables == null) {
            alteredTables = new ArrayList<Table>();
        }

        Table t = new Table(tableName);
        alteredTables.add(t);
        return t;
    }

    public Table renameTable(String tableName, String newName) {
        if (renamedTables == null) {
            renamedTables = new ArrayList<Table>();
        }

        Table t = new Table(tableName);
        t.setNewName(newName);
        renamedTables.add(t);
        return t;
    }

    public Table renameColumn(String tableName, String columnName, String newColumnName) {
        if (renamedColumns == null) {
            renamedColumns = new ArrayList<Column>();
        }

        Column c = new Column(columnName);
        c.setTableName(tableName);
        c.setNewName(newColumnName);
        renamedColumns.add(c);
        return null;
    }

    public Table createIndex(String tableName) {
        if (createdIndexes == null) {
            createdIndexes = new ArrayList<Table>();
        }

        Table t = new Table(tableName);
        t.createIndex();
        createdIndexes.add(t);
        return t;
    }

    public Table deleteTable(String tableName) {
        if (deletedTables == null) {
            deletedTables = new ArrayList<Table>();
        }

        Table t = new Table(tableName);
        deletedTables.add(t);
        return t;
    }

    public Table deleteColumn(String tableName, String columnName) {
        if (deletedColumns == null) {
            deletedColumns = new ArrayList<Column>();
        }

        Column c = new Column(columnName);
        c.setTableName(tableName);
        deletedColumns.add(c);
        return null;
    }

    public Table executeScript(String script) {
        if (scripts == null) {
            scripts = new ArrayList<String>();
        }

        scripts.add(script);
        return null;
    }

    public Table executeSql(String sql) {
        if (commands == null) {
            commands = new ArrayList<String>();
        }

        commands.add(sql);
        return null;
    }

    public List<Table> getAddedTables() {
        return addedTables;
    }

    public List<Table> getAlteredTables() {
        return alteredTables;
    }

    public List<Table> getRenamedTables() {
        return renamedTables;
    }

    public List<Column> getRenamedColumns() {
        return renamedColumns;
    }

    public List<Table> getCreatedIndexes() {
        return createdIndexes;
    }

    public List<Table> getDeletedTables() {
        return deletedTables;
    }

    public List<Column> getDeletedColumns() {
        return deletedColumns;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getScripts() {
        return scripts;
    }
}

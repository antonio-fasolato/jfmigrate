package net.fasolato.jfmigrate;

import net.fasolato.jfmigrate.builders.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fasolato on 04/04/2017.
 */
public class JFMigrationFluent {
    private List<Change> changes;

    public JFMigrationFluent() {
        changes = new ArrayList<Change>();
    }

    public Table createTable(String tableName) {
        Table t = new Table(tableName, OperationType.create);
        changes.add(t);
        return t;
    }

    public Table alterTable(String tableName) {
        Table t = new Table(tableName, OperationType.alter);
        changes.add(t);
        return t;
    }

    public Table renameTable(String tableName, String newName) {
        Table t = new Table(tableName, OperationType.rename);
        t.setNewName(newName);
        changes.add(t);
        return t;
    }

    public Table renameColumn(String tableName, String columnName, String newColumnName) {
        Column c = new Column(columnName, OperationType.rename);
        c.setTableName(tableName);
        c.setNewName(newColumnName);
        changes.add(c);
        return null;
    }

    public Index createIndex(String name) {
        Index i = new Index(name, OperationType.create);
        changes.add(i);
        return i;
    }

    public Table deleteTable(String tableName) {
        Table t = new Table(tableName, OperationType.delete);
        changes.add(t);
        return t;
    }

    public Column deleteColumn(String tableName, String columnName) {
        Column c = new Column(columnName, OperationType.delete);
        c.setTableName(tableName);
        changes.add(c);
        return c;
    }

    public Index deleteIndex(String name) {
        Index i = new Index(name, OperationType.delete);
        changes.add(i);
        return i;
    }

    public RawSql executeScript(String script) {
        RawSql r = new RawSql(script, true);
        changes.add(r);
        return r;
    }

    public RawSql executeSql(String sql) {
        RawSql r = new RawSql(sql, false);
        changes.add(r);
        return r;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public Data insert(Map<String, Object> data) {
        Data d = new Data(data);
        d.setOperationType(OperationType.insert);
        changes.add(d);
        return d;
    }
}

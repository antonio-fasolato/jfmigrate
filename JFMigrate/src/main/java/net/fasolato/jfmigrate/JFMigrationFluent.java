package net.fasolato.jfmigrate;

import net.fasolato.jfmigrate.builders.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Main class to start the fluent inerface to generate a migration definition.
 * All the interfaces perform no validation on the migration commands, the validity of the commands
 * must be checked against the choosen database engine
 */
public class JFMigrationFluent {
    private List<Change> changes;

    public JFMigrationFluent() {
        changes = new ArrayList<Change>();
    }

    /**
     * Method to create a table
     * @param tableName The table name. Must be a valid name for the target database engine
     * @return The Table object to continue the fluent chain
     */
    public Table createTable(String tableName) {
        Table t = new Table(tableName, OperationType.create);
        changes.add(t);
        return t;
    }

    /**
     * Method to start a command to alter a table
     * @param tableName The table to alter
     * @return The Table object to continue the fluent chain
     */
    public Table alterTable(String tableName) {
        Table t = new Table(tableName, OperationType.alter);
        changes.add(t);
        return t;
    }

    /**
     * Method to rename a table.
     * @param tableName The table to rename
     * @param newName The new table name
     * @return The Table object to continue the fluent chain
     */
    public Table renameTable(String tableName, String newName) {
        Table t = new Table(tableName, OperationType.rename);
        t.setNewName(newName);
        changes.add(t);
        return t;
    }

    /**
     * Method to rename a column.
     * @param tableName The table the column belongs to
     * @param columnName The column to rename
     * @param newColumnName The new column name
     * @return The Table object to continue the fluent chain
     */
    public Column renameColumn(String tableName, String columnName, String newColumnName) {
        Column c = new Column(columnName, OperationType.rename);
        c.setTableName(tableName);
        c.setNewName(newColumnName);
        changes.add(c);
        return c;
    }

    /**
     * Method to start the command to create an index
     * @param name The index name
     * @return The Index object to continue the fluent chain
     */
    public Index createIndex(String name) {
        Index i = new Index(name, OperationType.create);
        changes.add(i);
        return i;
    }

    /**
     * Method to delete a table (DROP)
     * @param tableName The table to delete
     * @return The Table object to continue the fluent chain
     */
    public Table deleteTable(String tableName) {
        Table t = new Table(tableName, OperationType.delete);
        changes.add(t);
        return t;
    }

    /**
     * Method to delete a column (DROP)
     * @param tableName The table the column belongs to
     * @param columnName The column to delete
     * @return The Column object to continue the fluent chain
     */
    public Column deleteColumn(String tableName, String columnName) {
        Column c = new Column(columnName, OperationType.delete);
        c.setTableName(tableName);
        changes.add(c);
        return c;
    }

    /**
     * Method to delete and index
     * @param name The index to delete
     * @return The Index object to continue the fluent chain
     */
    public Index deleteIndex(String name) {
        Index i = new Index(name, OperationType.delete);
        changes.add(i);
        return i;
    }

    /**
     * Method to execute an arbitrary SQL script
     * @param script The script
     * @return The RawSql object to continue the fluent chain
     */
    public RawSql executeScript(String script) {
        RawSql r = new RawSql(script, true);
        changes.add(r);
        return r;
    }

    /**
     * method to execute an arbitrary SQL command
     * @param sql The SQL command
     * @return The RawSql object to continue the fluent chain
     */
    public RawSql executeSql(String sql) {
        RawSql r = new RawSql(sql, false);
        changes.add(r);
        return r;
    }

    /**
     * List of changes added to this migration
     * @return The changes
     */
    public List<Change> getChanges() {
        return changes;
    }

    /**
     * Method to insert data in a table (the table name is specified in the rest of the fluent interface).<br>
     *  The method works on a list of Maps. Each element in the list is a row to be inserted.<br>
     *  Each Map in the list represents the data of the row, where each Map key is a column name and each value is the value to insert into the table.
     *  <br>
     *  <br>
     *  Example:<br>
     *  <pre>
     *  {@code
     *  List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
     *  Map<String, Object> values = new HashMap<String, Object>();
     *  values.put("id", 1);
     *  values.put("description", "test");
     *  data.add(values);
     *
     *  migration.insert(data).intoTable("test_tabel");
     *  }
     *  </pre>
     *  This will insert into the table test_tabel the following data:
     *  <pre>
     *     id  description
     *     1   test
     * </pre>
     * @param data The data to be inserted
     * @return The Data object to continue the fluent chain
     */
    public Data insert(List<Map<String, Object>> data) {
        Data d = new Data();
        d.data(data);
        d.setOperationType(OperationType.insert);
        changes.add(d);
        return d;
    }

    /**
     * Method to delete data from a table. The table name and where clause is specified in the rest of the fluent interface.
     * @return The Data object to continue the fluent chain
     */
    public Data delete() {
        Data d = new Data();
        d.setOperationType(OperationType.delete);
        changes.add(d);
        return d;
    }

    /**
     * Method to update data from a table.<br>
     *
     * @param data
     * @return
     */
    public Data update(List<Map<String, Object>> data) {
        Data d = new Data();
        d.data(data);
        d.setOperationType(OperationType.update);
        changes.add(d);
        return d;
    }
}

package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.JFException;
import net.fasolato.jfmigrate.internal.IDialectHelper;
import net.fasolato.jfmigrate.internal.Pair;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to express a table modification in a migration
 */
public class Table implements Change {
    private String name;
    private String newName;
    private OperationType operationType;
    private List<Column> changes;
    private List<ForeignKey> addedForeignKeys;

    /**
     * Constructoe
     * @param name Table name
     * @param operationType Type of operation (CREATE, DROP...)
     */
    public Table(String name, OperationType operationType) {
        this.name = name;
        this.operationType = operationType;
        addedForeignKeys = new ArrayList<ForeignKey>();
        changes = new ArrayList<Column>();
    }

    /**
     * Method to add a column to a table. After having called this method, the current column is the one just added,
     * so subsequent methods operaing on a column will target the new one.
     * @param columnName The new column name
     * @return The Table object for the fluent interface
     */
    public Table withColumn(String columnName) {
        Column c = new Column(columnName, OperationType.create);
        changes.add(c);
        return this;
    }

    /**
     * Method to create and ID column. The column has the following properties:
     * <ul>
     *     <li>Its name is "id"</li>
     *     <li>Its type is INTEGER</li>
     *     <li>It will be the table primary key</li>
     *     <li>It will be an identity column (sequence/autonumber where supported)</li>
     * </ul>
     * @return The Table object for the fluent interface
     */
    public Table withIdColumn() {
        Column c = new Column("id", OperationType.create);
        c.setType(JDBCType.INTEGER);
        changes.add(c);

        try {
            this.primaryKey();
            this.identity();
        } catch (JFException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * method to add a column in an alter table operation
     * @param columnName The new column name
     * @return The Table object for the fluent interface
     */
    public Table addColumn(String columnName) {
        Column c = new Column(columnName, OperationType.create);
        changes.add(c);
        return this;
    }

    /**
     * Method to specify the column to edit in an alter column operation
     * @param columnName The column to modify
     * @return The Table object for the fluent interface
     */
    public Table alterColumn(String columnName) {
        Column c = new Column(columnName, OperationType.alter);
        changes.add(c);
        return this;
    }
    /* Alter column */

    /**
     * Sets the current column as belonging to the table primary key
     * @return The Table object for the fluent interface
     */
    public Table primaryKey() {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }
        changes.get(changes.size() - 1).setPrimaryKey(true);
        return this;
    }

    /**
     * Method to add a foreign key to the table
     * @param keyName The foreign key name
     * @return The Table object for the fluent interface
     */
    public Table foreignKey(String keyName) {
        ForeignKey k = new ForeignKey(keyName);
        addedForeignKeys.add(k);
        return this;
    }

    /**
     * @param tableName The table representing the many side of the foreign key
     * @return The Table object for the fluent interface
     */
    public Table fromTable(String tableName) {
        if (addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        addedForeignKeys.get(addedForeignKeys.size() - 1).setFromTable(tableName);
        return this;
    }

    /**
     * @param tableName The table representing the one side of the foreign key
     * @return The Table object for the fluent interface
     */
    public Table toTable(String tableName) {
        if (addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        addedForeignKeys.get(addedForeignKeys.size() - 1).setToTable(tableName);
        return this;
    }

    /**
     * The foreign key column on the many side of the relationship
     * @param columnName The column name
     * @return The Table object for the fluent interface
     */
    public Table foreignColumn(String columnName) {
        return foreignColumns(columnName);
    }

    /**
     * The foreign key columns on the many side of the relationship
     * @param columnNames The list of column names
     * @return The Table object for the fluent interface
     */
    public Table foreignColumns(String... columnNames) {
        if (addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        ForeignKey fk = addedForeignKeys.get(addedForeignKeys.size() - 1);
        for(String c : columnNames) {
            fk.getForeignColumns().add(c);
        }
        return this;
    }

    /**
     * The primary key column referenced by the foreign key
     * @param columnName The primary column name
     * @return The Table object for the fluent interface
     */
    public Table primaryColumn(String columnName) {
        return primaryColumns(columnName);
    }

    /**
     * The primary key columns referenced by the foreign key
     * @param columnNames The list of primary column names
     * @return The Table object for the fluent interface
     */
    public Table primaryColumns(String... columnNames) {
        if (addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        ForeignKey fk = addedForeignKeys.get(addedForeignKeys.size() - 1);
        for(String c : columnNames) {
            fk.getPrimaryKeys().add(c);
        }
        return this;
    }

    /**
     * Sets the foreign key option "on delete cascade"
     * @return The Table object for the fluent interface
     */
    public Table onDeleteCascade() {
        if (addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        addedForeignKeys.get(addedForeignKeys.size() - 1).setOnDeleteCascade(true);
        return this;
    }

    /**
     * Sets the foreign key option "on update cascade"
     * @return The Table object for the fluent interface
     */
    public Table onUpdateCascade() {
        if (addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        addedForeignKeys.get(addedForeignKeys.size() - 1).setOnUpdateCascade(true);
        return this;
    }

    /**
     * Sets the current column as unique
     * @return The Table object for the fluent interface
     */
    public Table unique() {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        changes.get(changes.size() - 1).setUnique(true);
        return this;
    }

    /**
     * Sets the current column as nullable
     * @return The Table object for the fluent interface
     */
    public Table nullable() {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        changes.get(changes.size() - 1).setNullable(true);
        changes.get(changes.size() - 1).setNullableChanged(true);
        return this;
    }

    /**
     * Sets the current column as not nullable
     * @return The Table object for the fluent interface
     */
    public Table notNullable() {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        changes.get(changes.size() - 1).setNullable(false);
        changes.get(changes.size() - 1).setNullableChanged(true);
        return this;
    }

    /**
     * Sets the current column as identity (not all database dialect support this)
     * @return
     */
    public Table identity() {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        changes.get(changes.size() - 1).setIdentity(true);
        return this;
    }

    /**
     * Sets the current column default value
     * @param val The value (must be coherent with the column type)
     * @return The Table object for the fluent interface
     */
    public Table defaultValue(Object val) {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        changes.get(changes.size() - 1).setDefaultValueSet(true);
        changes.get(changes.size() - 1).setDefaultValue(val);
        return this;
    }

    /**
     * Sets the current column type as integer
     * @return The Table object for the fluent interface
     */
    public Table asInteger() {
        return asInteger(null);
    }

    /**
     * Sets the current column type as integer
     * @param precision The column precision
     * @return The Table object for the fluent interface
     */
    public Table asInteger(Integer precision) {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        Column c = changes.get(changes.size() - 1);
        c.setType(JDBCType.INTEGER);
        c.setPrecision(precision);
        c.setTypeChanged(true);

        return this;
    }

    /**
     * Sets the current column type as varchar
     * @return The Table object for the fluent interface
     */
    public Table asString() {
        return asString(null);
    }

    /**
     * Sets the current column type as string
     * @param precision The column length
     * @return The Table object for the fluent interface
     */
    public Table asString(Integer precision) {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        Column c = changes.get(changes.size() - 1);
        c.setType(JDBCType.VARCHAR);
        c.setPrecision(precision);
        c.setTypeChanged(true);

        return this;
    }

    /**
     * Sets the current column type as decimal
     * @return The Table object for the fluent interface
     */
    public Table asDecimal() {
        return asDecimal(null, null);
    }

    /**
     * Sets the current column type as decimal
     * @param precision The column precision
     * @return The Table object for the fluent interface
     */
    public Table asDecimal(Integer precision) {
        return asDecimal(precision, null);
    }

    /**
     * Sets the current column type as decimal
     * @param precision The column precision
     * @param scale The column scale
     * @return The Table object for the fluent interface
     */
    public Table asDecimal(Integer precision, Integer scale) {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        Column c = changes.get(changes.size() - 1);
        c.setType(JDBCType.DECIMAL);
        c.setPrecision(precision);
        c.setScale(scale);
        c.setTypeChanged(true);

        return this;
    }

    /**
     * Sets the current column type as a generic jdbc type
     * @param t The column type
     * @return The Table object for the fluent interface
     */
    public Table as(JDBCType t) {
        return as(t, null, null);
    }

    /**
     * Sets the current column type as a generic jdbc type
     * @param t The column type
     * @param precision The column precision
     * @return The Table object for the fluent interface
     */
    public Table as(JDBCType t, Integer precision) {
        return as(t, precision, null);
    }

    /**
     * Sets the current column type as a generic jdbc type
     * @param t The column type
     * @param precision The column precision
     * @param scale The column scale
     * @return The Table object for the fluent interface
     */
    public Table as(JDBCType t, Integer precision, Integer scale) {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        Column c = changes.get(changes.size() - 1);
        c.setType(t);
        c.setPrecision(precision);
        c.setScale(scale);
        c.setTypeChanged(true);

        return this;
    }

    /**
     * Sets the current column as auto-increment (dialect specific)
     * @return The Table object for the fluent interface
     */
    public Table autoIncrement() {
        return autoIncrement(1, 1);
    }

    /**
     * Sets the current column as auto-increment (dialect specific)
     * @param startWith Start value
     * @param step Increment step
     * @return The Table object for the fluent interface
     */
    public Table autoIncrement(long startWith, int step) {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        Column c = changes.get(changes.size() - 1);
        c.autoIncrement(startWith, step);

        return this;
    }

    /**
     * Internal method used in generating the SQL code to be executed
     * @param helper The database dialect helper class
     * @return The list of queries and optional data to execute
     */
    public List<Pair<String, Object[]>> getSqlCommand(IDialectHelper helper) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<Pair<String, Object[]>>();

        switch (operationType) {
            case create:
                return helper.getTableCreationCommand(this);
            case delete:
                for (String s : helper.getTableDropCommand(this)) {
                    toReturn.add(new Pair<String, Object[]>(s, null));
                }
                break;
            case rename:
                for (String s : helper.getTableRenameCommand(this)) {
                    toReturn.add(new Pair<String, Object[]>(s, null));
                }
                break;
            case alter:
                return helper.getAlterTableCommand(this);
        }

        return toReturn;
    }

    /* Columns */

    public String getName() {
        return name;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public List<ForeignKey> getAddedForeignKeys() {
        return addedForeignKeys;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public List<Column> getChanges() {
        return changes;
    }
}

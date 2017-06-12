package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.JFException;
import net.fasolato.jfmigrate.internal.IDialectHelper;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 20/03/2017.
 */
public class Table implements Change {
    private String name;
    private String newName;
    private OperationType operationType;
    private List<Column> changes;
    private List<ForeignKey> addedForeignKeys;

    public Table(String name, OperationType operationType) {
        this.name = name;
        this.operationType = operationType;
        addedForeignKeys = new ArrayList<ForeignKey>();
        changes = new ArrayList<Column>();
    }

    /* new column */
    public Table withColumn(String columnName) {
        Column c = new Column(columnName, OperationType.create);
        changes.add(c);
        return this;
    }

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
    /* new table */

    /* Alter column */
    public Table addColumn(String columnName) {
        Column c = new Column(columnName, OperationType.create);
        changes.add(c);
        return this;
    }

    public Table alterColumn(String columnName) {
        Column c = new Column(columnName, OperationType.alter);
        changes.add(c);
        return this;
    }
    /* Alter column */

    public Table primaryKey() {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }
        changes.get(changes.size() - 1).setPrimaryKey(true);
        return this;
    }

    /* Foreign key */
    public Table foreignKey(String keyName) {
        ForeignKey k = new ForeignKey(keyName);
        addedForeignKeys.add(k);
        return this;
    }

    public Table fromTable(String tableName) {
        if (addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        addedForeignKeys.get(addedForeignKeys.size() - 1).setFromTable(tableName);
        return this;
    }

    public Table toTable(String tableName) {
        if (addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        addedForeignKeys.get(addedForeignKeys.size() - 1).setToTable(tableName);
        return this;
    }

    public Table foreignColumn(String columnName) {
        if (addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        addedForeignKeys.get(addedForeignKeys.size() - 1).getForeignColumns().add(columnName);
        return this;
    }

    public Table primaryColumn(String columnName) {
        if (addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        addedForeignKeys.get(addedForeignKeys.size() - 1).getPrimaryKeys().add(columnName);
        return this;
    }
    /* Foreign key */

    /* Columns */
    public Table unique() {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        changes.get(changes.size() - 1).setUnique(true);
        return this;
    }

    public Table nullable() {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        changes.get(changes.size() - 1).setNullable(true);
        return this;
    }

    public Table notNullable() {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        changes.get(changes.size() - 1).setNullable(false);
        return this;
    }

    public Table identity() {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        changes.get(changes.size() - 1).setIdentity(true);
        return this;
    }

    public Table defaultValue(Object val) {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        changes.get(changes.size() - 1).setDefaultValue(val);
        return this;
    }

    /* Type definitions */
    public Table asInteger() {
        return asInteger(null);
    }

    public Table asInteger(Integer precision) {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        Column c = changes.get(changes.size() - 1);
        c.setType(JDBCType.INTEGER);
        c.setPrecision(precision);

        return this;
    }

    public Table asString() {
        return asString(null);
    }

    public Table asString(Integer precision) {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        Column c = changes.get(changes.size() - 1);
        c.setType(JDBCType.VARCHAR);
        c.setPrecision(precision);

        return this;
    }

    public Table as(JDBCType t) {
        return as(t, null, null);
    }

    public Table as(JDBCType t, Integer precision) {
        return as(t, precision, null);
    }

    public Table as(JDBCType t, Integer precision, Integer scale) {
        if (changes.isEmpty()) {
            throw new JFException("No column defined");
        }

        Column c = changes.get(changes.size() - 1);
        c.setType(t);
        c.setPrecision(precision);
        c.setScale(scale);

        return this;
    }

    public String[] getSqlCommand(IDialectHelper helper) {
        switch (operationType) {
            case create:
                return helper.getTableCreationCommand(this);
            case delete:
                return helper.getTableDropCommand(this);
            case rename:
                return helper.getTableRenameCommand(this);
            case alter:
                return helper.getAlterTableCommand(this);
            default:
                return new String[]{};
        }
    }

    /* Type definitions */
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

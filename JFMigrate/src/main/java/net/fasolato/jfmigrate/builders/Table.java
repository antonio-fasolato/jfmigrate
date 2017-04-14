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
    private List<Column> addedColumns;
    private List<Column> alteredColumns;
    private Column currentColumn;
    private List<ForeignKey> addedForeignKeys;

    public Table(String name, OperationType operationType) {
        this.name = name;
        this.operationType = operationType;
    }

    /* new column */
    public Table withColumn(String columnName) {
        if (addedColumns == null) {
            addedColumns = new ArrayList<Column>();
        }

        Column c = new Column(columnName, OperationType.create);
        addedColumns.add(c);
        currentColumn = c;
        return this;
    }

    public Table withIdColumn() {
        if (addedColumns == null) {
            addedColumns = new ArrayList<Column>();
        }

        Column c = new Column("id", OperationType.create);
        c.setType(JDBCType.INTEGER);
        addedColumns.add(c);
        currentColumn = c;

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
        if (addedColumns == null) {
            addedColumns = new ArrayList<Column>();
        }

        Column c = new Column(columnName, OperationType.create);
        addedColumns.add(c);
        currentColumn = c;
        return this;
    }

    public Table alterColumn(String columnName) {
        if (alteredColumns == null) {
            alteredColumns = new ArrayList<Column>();
        }

        Column c = new Column(columnName, OperationType.alter);
        alteredColumns.add(c);
        currentColumn = c;
        return this;
    }
    /* Alter column */

    public Table primaryKey() {
        if (currentColumn == null) {
            throw new JFException("No column defined");
        }

        currentColumn.setPrimaryKey(true);
        return this;
    }

    /* Foreign key */
    public Table foreignKey(String keyName) {
        if (addedForeignKeys == null) {
            addedForeignKeys = new ArrayList<ForeignKey>();
        }

        ForeignKey k = new ForeignKey(keyName);
        addedForeignKeys.add(k);
        return this;
    }

    public Table fromTable(String tableName) {
        if (addedForeignKeys == null || addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        addedForeignKeys.get(addedForeignKeys.size() - 1).setFromTable(tableName);
        return this;
    }

    public Table toTable(String tableName) {
        if (addedForeignKeys == null || addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        addedForeignKeys.get(addedForeignKeys.size() - 1).setToTable(tableName);
        return this;
    }

    public Table foreignColumn(String columnName) {
        if (addedForeignKeys == null || addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        addedForeignKeys.get(addedForeignKeys.size() - 1).getForeignColumns().add(columnName);
        return this;
    }

    public Table primaryColumn(String columnName) {
        if (addedForeignKeys == null || addedForeignKeys.isEmpty()) {
            throw new JFException("No foreign key defined");
        }

        addedForeignKeys.get(addedForeignKeys.size() - 1).getPrimaryKeys().add(columnName);
        return this;
    }
    /* Foreign key */

    /* Columns */
    public Table unique() {
        if (currentColumn == null) {
            throw new JFException("No column defined");
        }

        currentColumn.setUnique(true);
        return this;
    }

    public Table nullable() {
        if (currentColumn == null) {
            throw new JFException("No column defined");
        }

        currentColumn.setNullable(true);
        return this;
    }

    public Table notNullable() {
        if (currentColumn == null) {
            throw new JFException("No column defined");
        }

        currentColumn.setNullable(false);
        return this;
    }

    public Table identity() {
        if (currentColumn == null) {
            throw new JFException("No column defined");
        }

        currentColumn.setIdentity(true);
        return this;
    }

    public Table defaultValue(Object val) {
        if (currentColumn == null) {
            throw new JFException("No column defined");
        }

        currentColumn.setDefaultValue(val);
        return this;
    }

    /* Type definitions */
    public Table asInteger() {
        if (currentColumn == null) {
            throw new JFException("No column defined");
        }

        currentColumn.setType(JDBCType.INTEGER);

        return this;
    }

    public Table asString() {
        if (currentColumn == null) {
            throw new JFException("No column defined");
        }

        currentColumn.setType(JDBCType.VARCHAR);

        return this;
    }

    public Table as(JDBCType t) {
        if (currentColumn == null) {
            throw new JFException("No column defined");
        }

        currentColumn.setType(t);

        return this;
    }

    public String[] getSqlCommand(IDialectHelper helper) {
        switch (operationType) {
            case create:
                return helper.getTableCreationCommand(this);
            default:
                return new String[] {};
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

    public List<Column> getAddedColumns() {
        return addedColumns;
    }

    public List<Column> getAlteredColumns() {
        return alteredColumns;
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
}

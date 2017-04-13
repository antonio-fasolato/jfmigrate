package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.JFException;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 20/03/2017.
 */
public class Table {
    private String name;
    private String newName;
    private List<Column> addedColumns;
    private List<Column> alteredColumns;
    private Column currentColumn;
    private List<ForeignKey> addedForeignKeys;
    private List<Index> createdIndexes;

    public Table(String name) {
        this.name = name;
    }

    /* new column */
    public Table withColumn(String columnName) {
        if (addedColumns == null) {
            addedColumns = new ArrayList<Column>();
        }

        Column c = new Column(columnName);
        addedColumns.add(c);
        currentColumn = c;
        return this;
    }

    public Table withIdColumn() {
        if (addedColumns == null) {
            addedColumns = new ArrayList<Column>();
        }

        Column c = new Column("id");
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

        Column c = new Column(columnName);
        addedColumns.add(c);
        currentColumn = c;
        return this;
    }

    public Table alterColumn(String columnName) {
        if (alteredColumns == null) {
            alteredColumns = new ArrayList<Column>();
        }

        Column c = new Column(columnName);
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
    public Table foreignKey() {
        if (addedForeignKeys == null) {
            addedForeignKeys = new ArrayList<ForeignKey>();
        }

        ForeignKey k = new ForeignKey();
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

    /* Indexes */
    public Table createIndex() {
        if (createdIndexes == null) {
            createdIndexes = new ArrayList<Index>();
        }

        createdIndexes.add(new Index());
        return this;
    }

    public Table withIndexedColumn(String columnName) {
        if (createdIndexes == null || createdIndexes.isEmpty()) {
            throw new JFException("No index defined");
        }

        createdIndexes.get(createdIndexes.size() - 1).getColumns().add(columnName);
        return this;
    }
    /* Indexes */

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
}

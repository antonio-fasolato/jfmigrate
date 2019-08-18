package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.JFMigrationFluent;
import net.fasolato.jfmigrate.internal.IDialectHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to reopresent a change on a table data (insert, delete, update)
 */
public class Data implements Change {
    private OperationType operationType;
    private List<Map<String, Object>> data;
    private String tableName;
    private boolean allRows;
    private List<Map<String, Object>> where;

    /**
     * Setst the destination table for an insert operation
     * @param tableName The destination table name
     * @return The Data object for the fluent interface
     */
    public Data intoTable(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * The target table for a delete operation
     * @param tableName The target table name
     * @return The Data object for the fluent interface
     */
    public Data fromTable(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * The target table of an update operation
     * @param tableName The target table name
     * @return The Data object for the fluent interface
     */
    public Data table(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * Method to pass values to insert or update operations
     * @param data The data. See {@link net.fasolato.jfmigrate.JFMigrationFluent#insert(List)} for its use.
     * @return The Data object for the fluent interface
     */
    public Data data(List<Map<String, Object>> data) {
        this.data = data;
        return this;
    }

    /**
     * Mwthod to set the where clause on an update/delete operation. Used by {@link JFMigrationFluent#delete()} and {@link JFMigrationFluent#update(List)}
     * @param where The list of key/values (column names, values) to build the where clause
     * @return The Data object for the fluent interface
     */
    public Data where(List<Map<String, Object>> where) {
        this.setWhere(where);
        return this;
    }

    /**
     * Mwthod to set the where clause on an update/delete operation. In this case an empty where, so to operate an all rows.
     * Used by {@link JFMigrationFluent#delete()} and {@link JFMigrationFluent#update(List)}
     * @return The Data object for the fluent interface
     */
    public Data allRows() {
        allRows = true;
        return this;
    }

    /**
     * Internal method used in generating the SQL code to be executed
     * @param helper The database dialect helper class
     * @return The list of queries and optional data to execute
     */
    public List<Pair<String, Object[]>> getSqlCommand(IDialectHelper helper) {
        switch (operationType) {
            case insert:
                return helper.getInsertCommand(this);
            case delete:
                return helper.getDeleteCommand(this);
            case update:
                return helper.getUpdateCommand(this);
            default:
                return new ArrayList<>();
        }
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public String getTableName() {
        return tableName;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public boolean isAllRows() {
        return allRows;
    }

    public void setAllRows(boolean allRows) {
        this.allRows = allRows;
    }

    public List<Map<String, Object>> getWhere() {
        return where;
    }

    public void setWhere(List<Map<String, Object>> where) {
        this.where = where;
    }
}

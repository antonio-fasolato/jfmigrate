package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.internal.IDialectHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to express an index in an index creation operation
 */
public class Index implements Change {
    private String name;
    private String tableName;
    private List<String> columns;
    private boolean unique;
    private OperationType operationType;

    /**
     * Contructor
     * @param name The index name
     * @param operationType The operation type (create/delete)
     */
    public Index(String name, OperationType operationType) {
        this.name = name;
        this.operationType = operationType;
        columns = new ArrayList<String>();
    }

    /**
     * Method to add a column (from the target table) to the index
     * @param columnName The column name
     * @return The Index object for the fluent interface
     */
    public Index withIndexedColumn(String columnName) {
        getColumns().add(columnName);
        return this;
    }

    /**
     * Function to set the index target table name.
     * @param tableName The table name
     * @return The Index object for the fluent interface
     */
    public Index fromTable(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * Sets the index as unique
     * @return The Index object for the fluent interface
     */
    public Index unique() {
        this.unique = true;
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
                for (String s : helper.getIndexCreationCommand(this)) {
                    toReturn.add(new ImmutablePair<>(s, null));
                }
                break;
            case delete:
                for (String s : helper.getIndexDropCommand(this)) {
                    toReturn.add(new ImmutablePair<String, Object[]>(s, null));
                }
                break;
        }

        return toReturn;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }
}

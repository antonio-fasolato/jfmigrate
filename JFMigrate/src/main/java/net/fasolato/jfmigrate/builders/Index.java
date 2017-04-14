package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.internal.IDialectHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 04/04/2017.
 */
public class Index implements Change {
    private String name;
    private String tableName;
    private List<String> columns;
    private boolean unique;
    private OperationType operationType;

    public Index(String name, OperationType operationType) {
        this.name = name;
        this.operationType = operationType;
        columns = new ArrayList<String>();
    }

    public Index withIndexedColumn(String columnName) {
        getColumns().add(columnName);
        return this;
    }

    public Index fromTable(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Index unique() {
        this.unique = true;
        return this;
    }

    public String[] getSqlCommand(IDialectHelper helper) {
        switch (operationType) {
            case create:
                return helper.getIndexCreationCommand(this);
            default:
                return new String[]{};
        }
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

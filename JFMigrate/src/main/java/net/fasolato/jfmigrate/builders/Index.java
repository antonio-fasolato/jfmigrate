package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.JFException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 04/04/2017.
 */
public class Index implements Change {
    private String tableName;
    private List<String> columns;
    private OperationType operationType;

    public Index(OperationType operationType) {
        this.operationType = operationType;
        columns = new ArrayList<String>();
    }

    public Index withIndexedColumn(String columnName) {
        getColumns().add(columnName);
        return this;
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
}

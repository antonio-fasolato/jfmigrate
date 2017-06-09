package net.fasolato.jfmigrate.builders;

import net.fasolato.jfmigrate.internal.IDialectHelper;

import java.util.Map;

/**
 * Created by fasolato on 20/04/2017.
 */
public class Data implements Change {
    private OperationType operationType;
    private Map<String, Object> data;
    private String tableName;
    private boolean allRows;
    private Map where;
    private Object[] values;

    public Data(Map data) {
        this.data = data;
    }

    public Data intoTable(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Data fromTable(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public String[] getSqlCommand(IDialectHelper helper) {
        switch (operationType) {
            case insert:
                Map.Entry<String[], Object[]> toReturn = helper.getInsertCommand(this);
                values = toReturn.getValue();
                return toReturn.getKey();
            default:
                return new String[0];
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

    public boolean isAllRows() {
        return allRows;
    }

    public void setAllRows(boolean allRows) {
        this.allRows = allRows;
    }

    public Map getWhere() {
        return where;
    }

    public void setWhere(Map where) {
        this.where = where;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Object[] getValues() {
        return values;
    }
}

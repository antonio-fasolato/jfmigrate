package net.fasolato.jfmigrate.builders;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 04/04/2017.
 */
public class Index {
    private String tableName;
    private List<String> columns;

    public Index() {
        columns = new ArrayList<String>();
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
}

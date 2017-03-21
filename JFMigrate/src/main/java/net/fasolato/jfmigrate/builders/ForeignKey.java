package net.fasolato.jfmigrate.builders;

/**
 * Created by fasolato on 21/03/2017.
 */
public class ForeignKey {
    private String tableName;
    private String columnName;

    public ForeignKey(String tableName, String columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }
}

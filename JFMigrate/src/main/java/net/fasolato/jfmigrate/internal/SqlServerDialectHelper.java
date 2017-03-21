package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.Column;
import net.fasolato.jfmigrate.builders.Table;

/**
 * Created by fasolato on 21/03/2017.
 */
public class SqlServerDialectHelper implements IDialectHelper {
    public String tableCreation(String databaseName, String schemaName, Table t) {
        String sql = "";

        sql += " CREATE TABLE ";
        sql += databaseName != null ? databaseName + "." : "";
        sql += schemaName != null ? schemaName + "." : "";
        sql += t.getName();
        sql += " ( ";
        int i = 0;
        for (Column c : t.getColumns()) {
            i++;
            sql += c.getName() + " " + c.getType() + " ";
            if (c.getPrecision() != null) {
                sql += "(" + c.getPrecision();
                sql += c.getScale() != null ? "," + c.getScale() : "";
                sql += ")";
            }
            sql += c.isPrimaryKey() ? " PRIMARY KEY " : "";
            sql += c.isUnique() ? " UNIQUE " : "";
            sql += c.isNullable() ? "" : " NOT NULL ";
            if (c.getForeignKey() != null) {
                sql += " FOREIGN KEY REFERENCES " + c.getForeignKey().getTableName();
                sql += c.getForeignKey().getColumnName() != null ? "(" + c.getForeignKey().getColumnName() + ")" : "";
                sql += " ";
            }
            if(i < t.getColumns().size()) {
                sql += ", ";
            }
        }
        sql += " );";

        return sql;
    }

    public String tableDropping(String databaseName, String schemaName, Table t) {
        String sql = "";

        sql += " DROP TABLE ";
        sql += databaseName != null ? databaseName + "." : "";
        sql += schemaName != null ? schemaName + "." : "";
        sql += t.getName();
        sql += ";";

        return sql;
    }
}

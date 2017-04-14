package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.Column;
import net.fasolato.jfmigrate.builders.ForeignKey;
import net.fasolato.jfmigrate.builders.Index;
import net.fasolato.jfmigrate.builders.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasolato on 21/03/2017.
 */
public class SqlServerDialectHelper implements IDialectHelper {
    public String getDatabaseVersionCommand() {
        String sql = "";

        sql += "IF (EXISTS (";
        sql += "	SELECT * ";
        sql += "    FROM INFORMATION_SCHEMA.TABLES ";
        sql += "    WHERE 1 = 1";
//        sql += "		and TABLE_SCHEMA = 'dbo' ";
        sql += "        AND  TABLE_NAME = '" + JFMigrationConstants.DB_VERSION_TABLE_NAME + "'";
        sql += "	))";
        sql += "    select isnull(max(version), 0) as version from " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";
        sql += "else";
        sql += "	select -1 as version";

        return sql;
    }

    public String getVersionTableCreationCommand() {
        String sql = "";

        sql += "CREATE TABLE [dbo].[" + JFMigrationConstants.DB_VERSION_TABLE_NAME + "](";
        sql += "	[version] [int] NOT NULL,";
        sql += "	[appliedat] [datetime] NOT NULL,";
        sql += "	[migrationname] [nvarchar](255) NOT NULL,";
        sql += " CONSTRAINT [PK_jfmigratedbversion] PRIMARY KEY CLUSTERED ";
        sql += "(";
        sql += "	[version] ASC";
        sql += "))";

        return sql;
    }

    public String getInsertNewVersionCommand() {
        String sql = "";

        sql += "insert into " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";
        sql += "	(version, appliedat, migrationname)";
        sql += "values";
        sql += "	(?, GETDATE(), ?)";

        return sql;
    }

    public String[] getTableCreationCommand(Table t) {
        List<String> toReturn = new ArrayList<String>();
        String sql = "";

        sql += " CREATE TABLE ";
        sql += t.getName();
        sql += " ( ";
        int i = 0;
        for (Column c : t.getAddedColumns()) {
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
//            if (c.getForeignKey() != null) {
//                sql += " FOREIGN KEY REFERENCES " + c.getForeignKey().getTableName();
//                sql += c.getForeignKey().getColumnName() != null ? "(" + c.getForeignKey().getColumnName() + ")" : "";
//                sql += " ";
//            }
            if (i < t.getAddedColumns().size()) {
                sql += ", ";
            }
        }
        sql += " );";
        toReturn.add(sql);

        for (ForeignKey k : t.getAddedForeignKeys()) {
            sql = "";
            sql += "ALTER TABLE " + k.getFromTable() + " ";
            sql += "ADD CONSTRAINT " + k.getName() + " FOREIGN KEY ( ";
            for (i = 0; i < k.getForeignColumns().size(); i++) {
                String c = k.getForeignColumns().get(i);
                sql += " " + c;
                if (i < k.getForeignColumns().size() - 1) {
                    sql += ", ";
                }
            }
            sql += ") ";
            sql += "    REFERENCES " + k.getToTable() + " ( ";
            for (i = 0; i < k.getPrimaryKeys().size(); i++) {
                String c = k.getPrimaryKeys().get(i);
                sql += " " + c;
                if (i < k.getPrimaryKeys().size() - 1) {
                    sql += ", ";
                }
            }
            sql += ") ";
        }
        toReturn.add(sql);

        return toReturn.toArray(new String[toReturn.size()]);
    }

    public String[] getIndexCreationCommand(Index i) {
        List<String> toReturn = new ArrayList<String>();

        String sql = "";
        sql += " CREATE ";
        if (i.isUnique()) {
            sql += " UNIQUE ";
        }
        sql += " INDEX " + i.getName() + " ON " + i.getTableName() + " ( ";
        for (int j = 0; j < i.getColumns().size(); j++) {
            sql += " " + i.getColumns().get(j);
            if (j < i.getColumns().size() - 1) {
                sql += ", ";
            }
        }
        sql += " ) ";
        toReturn.add(sql);

        return toReturn.toArray(new String[toReturn.size()]);
    }

    public String tableDropping(String databaseName, String schemaName, Table t) {
        String sql = "";

//        sql += " DROP TABLE ";
//        sql += databaseName != null ? databaseName + "." : "";
//        sql += schemaName != null ? schemaName + "." : "";
//        sql += t.getName();
//        sql += ";";

        return sql;
    }
}

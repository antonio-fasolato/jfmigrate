package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class H2DialectHelper implements IDialectHelper {
    public String getDatabaseVersionTableExistenceCommand() {
        String sql = "";

        sql += " SELECT COUNT(*) AS count  ";
        sql += " FROM information_schema.tables  ";
        sql += " WHERE 1 = 1  ";
        sql += "   and table_name = '" + JFMigrationConstants.DB_VERSION_TABLE_NAME.toUpperCase() + "' ";

        return sql;
    }

    public String getDatabaseVersionCommand() {
        String sql = "";

        sql += " select ifnull(max(version), 0) ";
        sql += " from " + JFMigrationConstants.DB_VERSION_TABLE_NAME;

        return sql;
    }

    public String getSearchDatabaseVersionCommand() {
        String sql = "";

        sql += " select version  ";
        sql += " from jfmigratedbversion  ";
        sql += " where 1 = 1 ";
        sql += " 	and version = ? ";

        return sql;
    }

    public String getVersionTableCreationCommand() {
        String sql = "";

        sql += " create table " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ( ";
        sql += "   version int primary key, ";
        sql += "   appliedat timestamp not null, ";
        sql += "   migrationname varchar(255) not null ";
        sql += " ) ";

        return sql;
    }

    public String getInsertNewVersionCommand() {
        String sql = "";

        sql += "insert into " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";
        sql += "	(version, appliedat, migrationname)";
        sql += "values";
        sql += "	(?, CURRENT_TIMESTAMP(), ?)";

        return sql;
    }

    public String getDeleteVersionCommand() {
        String sql = "";

        sql += " delete from " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";
        sql += " where 1 = 1 ";
        sql += "	and version = ? ";

        return sql;
    }

    public String[] getTableCreationCommand(Table t) {
        List<String> toReturn = new ArrayList<String>();
        String sql = "";

        sql += " CREATE TABLE ";
        sql += t.getName();
        sql += " ( ";
        int i = 0;
        for (Column c : t.getChanges()) {
            i++;
            if (c.getOperationType() == OperationType.create) {
                sql += c.getName() + " " + c.getType() + " ";
                if (c.getPrecision() != null) {
                    sql += "(" + c.getPrecision();
                    sql += c.getScale() != null ? "," + c.getScale() : "";
                    sql += ")";
                }
                sql += c.isPrimaryKey() ? " PRIMARY KEY " : "";
                sql += c.isUnique() ? " UNIQUE " : "";
                sql += c.isNullable() ? "" : " NOT NULL ";
                if (i < t.getChanges().size()) {
                    sql += ", ";
                }
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
            toReturn.add(sql);
        }

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

    public String[] getTableDropCommand(Table t) {
        return new String[0];
    }

    public String[] getIndexDropCommand(Index i) {
        return new String[0];
    }

    public String[] getColumnDropCommand(Column c) {
        return new String[0];
    }

    public String[] getTableRenameCommand(Table t) {
        return new String[0];
    }

    public String[] getColumnRenameCommand(Column c) {
        return new String[0];
    }

    public String[] getAlterTableCommand(Table t) {
        return new String[0];
    }

    public Map.Entry<String[], Object[]> getInsertCommand(Data d) {
        return null;
    }
}

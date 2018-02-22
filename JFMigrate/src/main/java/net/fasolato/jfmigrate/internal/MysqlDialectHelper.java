package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.JFException;
import net.fasolato.jfmigrate.builders.*;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

public class MysqlDialectHelper implements IDialectHelper {

    private String schema;

    public MysqlDialectHelper(String schema) {
        if (schema == null || "".equals(schema)) {
            throw new JFException("Schema is null or empty. Mysql dialect needs a schema name");
        }

        this.schema = schema;
    }

    public String getDatabaseVersionTableExistenceCommand() {
        String sql = "";

        sql += "	SELECT count(*) as count ";
        sql += "	FROM information_schema.tables ";
        sql += "	WHERE 1 = 1 ";
        sql += "		AND table_schema = '" + schema + "' ";
        sql += "	    AND table_name = '" + JFMigrationConstants.DB_VERSION_TABLE_NAME + "' ";
        sql += "	LIMIT 1; ";

        return sql;
    }

    public String getDatabaseVersionCommand() {
        String sql = "";

        sql += "select coalesce(max(version), 0) as version from " + schema + "." + JFMigrationConstants.DB_VERSION_TABLE_NAME + "; ";

        return sql;
    }

    public String getSearchDatabaseVersionCommand() {
        String sql = "";

        sql += " select version  ";
        sql += " from " + schema + "." + JFMigrationConstants.DB_VERSION_TABLE_NAME;
        sql += " where 1 = 1 ";
        sql += " 	and version = ? ";
        sql += ";";

        return sql;
    }

    public String getVersionTableCreationCommand() {
        String sql = "";

        sql += "CREATE TABLE " + schema + "." + JFMigrationConstants.DB_VERSION_TABLE_NAME + "(";
        sql += "	version bigint(20) NOT NULL,";
        sql += "	appliedat timestamp NOT NULL,";
        sql += "	migrationname varchar(255) NOT NULL,";
        sql += " PRIMARY KEY(version) ";
        sql += ")";

        return sql;
    }

    public String getInsertNewVersionCommand() {
        String sql = "";

        sql += "insert into " + schema + "." + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";
        sql += "	(version, appliedat, migrationname)";
        sql += "values";
        sql += "	(?, CURRENT_TIMESTAMP(), ?);";

        return sql;
    }

    public String getDeleteVersionCommand() {
        String sql = "";

        sql += " delete from " + schema + "." + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";
        sql += " where 1 = 1 ";
        sql += "	and version = ? ";
        sql += ";";

        return sql;
    }

    public String[] getScriptCheckMigrationUpVersionCommand() {
        return new String[0];
    }

    public String[] getScriptCheckMigrationDownVersionCommand() {
        return new String[0];
    }

    public String[] getTableCreationCommand(Table t) {
        List<String> toReturn = new ArrayList<String>();
        String sql = "";
        List<String> primaryKeys = new ArrayList<String>();

        sql += " CREATE TABLE ";
        sql += t.getName();
        sql += " ( ";
        int i = 0;
        for (Column c : t.getChanges()) {
            i++;
            if (c.getOperationType() == OperationType.create) {
                sql += c.getName() + " ";
                if (c.getType().equals(JDBCType.BOOLEAN)) {
                    sql += "BIT ";
                } else if (c.getType().equals(JDBCType.TIMESTAMP)) {
                    sql += "DATETIME ";
                } else {
                    sql += c.getType() + " ";
                }
                if (c.getPrecision() != null) {
                    sql += "(" + c.getPrecision();
                    sql += c.getScale() != null ? "," + c.getScale() : "";
                    sql += ")";
                }
                if (c.getPrecision() == null && c.getType().equals(JDBCType.VARCHAR)) {
                    throw new JFException("VARCHAR size is required in MySql");
                }
                sql += c.isUnique() ? " UNIQUE " : "";
                sql += c.isNullable() ? "" : " NOT NULL ";
                if (i < t.getChanges().size()) {
                    sql += ", ";
                }
                if (c.isPrimaryKey()) {
                    primaryKeys.add(c.getName());
                }
            }
        }
        if (!primaryKeys.isEmpty()) {
            sql += " , primary key (";
            int k = 1;
            for (String c : primaryKeys) {
                sql += c;
                if (k < primaryKeys.size()) {
                    sql += ", ";
                }
                k++;
            }
            sql += ") ";
        }

        for (ForeignKey k : t.getAddedForeignKeys()) {
            sql += ", FOREIGN KEY " + k.getName() + " (";
            for (i = 0; i < k.getForeignColumns().size(); i++) {
                String c = k.getForeignColumns().get(i);
                sql += " " + c;
                if (i < k.getForeignColumns().size() - 1) {
                    sql += ", ";
                }
            }
            sql += ") ";
            sql += " REFERENCES " + k.getFromTable() + "( ";
            for (i = 0; i < k.getPrimaryKeys().size(); i++) {
                String c = k.getPrimaryKeys().get(i);
                sql += " " + c;
                if (i < k.getPrimaryKeys().size() - 1) {
                    sql += ", ";
                }
            }
            sql += " ) ";

            if (k.isOnDeleteCascade()) {
                sql += " ON DELETE CASCADE ";
            }
            if (k.isOnUpdateCascade()) {
                sql += " ON UPDATE CASCADE ";
            }
        }

        sql += " );";
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
        sql += " ); ";
        toReturn.add(sql);

        return toReturn.toArray(new String[toReturn.size()]);
    }

    public String[] getTableDropCommand(Table t) {
        String sql = "";

        sql += " DROP TABLE " + t.getName() + " ;";

        return new String[]{sql};
    }

    public String[] getIndexDropCommand(Index i) {
        String sql = "";

        sql += " DROP INDEX " + i.getName() + " ON " + i.getTableName() + " ;";

        return new String[]{sql};
    }

    public String[] getColumnDropCommand(Column c) {
        String sql = "";

        sql += " ALTER TABLE " + c.getTableName() + " DROP COLUMN " + c.getName() + " ;";

        return new String[]{sql};
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

    public List<Pair<String, Object[]>> getInsertCommand(Data d) {
        return null;
    }

    public List<Pair<String, Object[]>> getDeleteCommand(Data d) {
        return null;
    }

    public List<Pair<String, Object[]>> getUpdateCommand(Data d) {
        return null;
    }
}

package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fasolato on 21/03/2017.
 */
public class SqlServerDialectHelper implements IDialectHelper {
    private static Logger log = LogManager.getLogger(SqlServerDialectHelper.class);

    public String getDatabaseVersionTableExistenceCommand() {
        String sql = "";

        sql += "	SELECT COUNT(*) as count ";
        sql += "    FROM INFORMATION_SCHEMA.TABLES ";
        sql += "    WHERE 1 = 1";
//        sql += "		and TABLE_SCHEMA = 'dbo' ";
        sql += "        AND  TABLE_NAME = '" + JFMigrationConstants.DB_VERSION_TABLE_NAME + "'";

        return sql;
    }

    public String getDatabaseVersionCommand() {
        String sql = "";

        sql += "select isnull(max(version), 0) as version from " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";

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

        sql += "CREATE TABLE [dbo].[" + JFMigrationConstants.DB_VERSION_TABLE_NAME + "](";
        sql += "	[version] [bigint] NOT NULL,";
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
            sql += " ) ";
            sql += "    REFERENCES " + k.getToTable() + " ( ";
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
        String sql = "";

        log.error("Warning: Microsoft recommends dropping and recreating the table not to break scripts and stored procedures. See https://docs.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-rename-transact-sql");
        sql += " EXEC sp_rename '" + t.getName() + "' , '" + t.getNewName() + "' ;";

        return new String[]{sql};
    }

    public String[] getColumnRenameCommand(Column c) {
        String sql = "";

        log.error("Warning: Microsoft recommends dropping and recreating the table not to break scripts and stored procedures. See https://docs.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-rename-transact-sql");
        sql += " EXEC sp_rename '" + c.getTableName() + "." + c.getName() + "' , '" + c.getNewName() + "', 'COLUMN' ;";

        return new String[]{sql};
    }

    public String[] getAlterTableCommand(Table t) {
        List<String> toReturn = new ArrayList<String>();

        for (Column c : t.getChanges()) {
            String sql = "";
            if (c.getOperationType() == OperationType.create) {
                sql += " ALTER TABLE ";
                sql += t.getName();
                sql += " ADD ";
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
                sql += c.isPrimaryKey() ? " PRIMARY KEY " : "";
                sql += c.isUnique() ? " UNIQUE " : "";
                sql += c.isNullable() ? "" : " NOT NULL ";
            } else if (c.getOperationType() == OperationType.alter) {
                sql += " ALTER TABLE ";
                sql += t.getName();
                sql += " ALTER COLUMN ";
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
                sql += c.isPrimaryKey() ? " PRIMARY KEY " : "";
                sql += c.isUnique() ? " UNIQUE " : "";
                sql += c.isNullable() ? "" : " NOT NULL ";
            }
            toReturn.add(sql);
        }

        return toReturn.toArray(new String[toReturn.size()]);
    }

    public List<Pair<String, Object[]>> getInsertCommand(Data d) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<Pair<String, Object[]>>();

        for (Map<String, Object> m : d.getData()) {
            String sql = "";
            List<Object> values = new ArrayList<Object>();

            sql += " INSERT INTO " + d.getTableName() + " (";
            int i = 0;
            for (String k : m.keySet()) {
                sql += k;
                if (i < m.keySet().size() - 1) {
                    sql += ", ";
                }
                i++;
            }
            sql += " ) VALUES (";
            i = 0;
            for (String k : m.keySet()) {
                sql += "?";
                values.add(m.get(k));
                if (i < m.keySet().size() - 1) {
                    sql += ", ";
                }
                i++;
            }
            sql += " ) ";

            toReturn.add(new Pair<String, Object[]>(sql, values.toArray()));
        }

        return toReturn;
    }

    public List<Pair<String, Object[]>> getDeleteCommand(Data d) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<Pair<String, Object[]>>();

        for (Map<String, Object> w : d.getDeleteWhere()) {
            String sql = "";
            List<Object> values = new ArrayList<Object>();

            sql += " DELETE " + d.getTableName() + " WHERE 1 = 1 ";
            for (String k : w.keySet()) {
                sql += " AND " + k + " = ? ";
                values.add(w.get(k));
            }

            toReturn.add(new Pair<String, Object[]>(sql, values.toArray()));
        }

        return toReturn;
    }

    public List<Pair<String, Object[]>> getUpdateCommand(Data d) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<Pair<String, Object[]>>();

        for (int i = 0; i < d.getData().size(); i++) {
            Map<String, Object> m = d.getData().get(i);

            String sql = "";
            List<Object> values = new ArrayList<Object>();

            sql += " UPDATE " + d.getTableName() + " SET ";
            int j = 0;
            for (String k : m.keySet()) {
                sql += k + " = ? ";
                values.add(m.get(k));
                if (j < m.keySet().size() - 1) {
                    sql += ", ";
                }
                j++;
            }
            sql += " WHERE 1 = 1 ";
            for (Map<String, Object> w : d.getDeleteWhere()) {
                for (String k : w.keySet()) {
                    sql += " AND " + k + " = ? ";
                    values.add(w.get(k));
                }
            }

            toReturn.add(new Pair<String, Object[]>(sql, values.toArray()));
        }

        return toReturn;
    }
}

package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.JFException;
import net.fasolato.jfmigrate.builders.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fasolato on 21/03/2017.
 */
public class SqlServerDialectHelper extends GenericDialectHelper implements IDialectHelper {
    private static Logger log = LogManager.getLogger(SqlServerDialectHelper.class);

    public String getDatabaseVersionTableExistenceCommand() {
        String sql = "";

        sql += "	SELECT COUNT(*) as count ";
        sql += "    FROM INFORMATION_SCHEMA.TABLES ";
        sql += "    WHERE 1 = 1";
//        sql += "		and TABLE_SCHEMA = 'dbo' ";
        sql += "        AND  TABLE_NAME = '" + JFMigrationConstants.DB_VERSION_TABLE_NAME + "';";

        return sql;
    }

    public String getDatabaseVersionCommand() {
        String sql = "";

        sql += "select isnull(max(version), 0) as version from " + JFMigrationConstants.DB_VERSION_TABLE_NAME + "; ";

        return sql;
    }

    public String getSearchDatabaseVersionCommand() {
        String sql = "";

        sql += " select version  ";
        sql += " from jfmigratedbversion  ";
        sql += " where 1 = 1 ";
        sql += " 	and version = ? ";
        sql += ";";

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
        sql += "));";

        return sql;
    }

    @Override
    public String getVersionTableDeleteCommand() {
        return String.format(" DROP TABLE %s; ", JFMigrationConstants.DB_VERSION_TABLE_NAME);
    }

    public String getInsertNewVersionCommand() {
        String sql = "";

        sql += "insert into " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";
        sql += "	(version, appliedat, migrationname)";
        sql += "values";
        sql += "	(?, GETDATE(), ?);";

        return sql;
    }

    public String getDeleteVersionCommand() {
        String sql = "";

        sql += " delete from " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";
        sql += " where 1 = 1 ";
        sql += "	and version = ? ";
        sql += ";";

        return sql;
    }

    public String[] getScriptCheckMigrationUpVersionCommand() {
        List<String> toReturn = new ArrayList<String>();
        String sql = "";

        sql += "IF not exists (select * from jfmigratedbversion where version = ?) \n";
        sql += "BEGIN \n";
        toReturn.add(sql);

        sql = "";
        sql += "END \n";
        toReturn.add(sql);

        return toReturn.toArray(new String[toReturn.size()]);
    }

    public String[] getScriptCheckMigrationDownVersionCommand() {
        List<String> toReturn = new ArrayList<String>();
        String sql = "";

        sql += "IF exists (select * from jfmigratedbversion where version = ?) \n";
        sql += "BEGIN \n";
        toReturn.add(sql);

        sql = "";
        sql += "END \n";
        toReturn.add(sql);

        return toReturn.toArray(new String[toReturn.size()]);
    }

    public List<Pair<String, Object[]>> getTableCreationCommand(Table t) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();
        List<String> primaryKeys = new ArrayList<>();
        String sql = "";

        sql += " CREATE TABLE ";
        sql += t.getName();
        sql += " ( ";
        int i = 0;
        for (Column c : t.getChanges()) {
            i++;
            if (c.getOperationType() == OperationType.create) {
                sql += c.getName() + " ";
                if(c.isAutoIncrement() && c.getType() == null && c.getRawType() == null) {
                    c.setType(JDBCType.INTEGER);
                    c.setTypeChanged(true);
                }
                if(c.getRawType() == null) {
                    if (c.getType().equals(JDBCType.BOOLEAN)) {
                        sql += "BIT ";
                    } else if (c.getType().equals(JDBCType.TIMESTAMP)) {
                        sql += "DATETIME ";
                    } else {
                        sql += c.getType() + " ";
                    }
                } else {
                    sql += c.getRawType() + " ";
                }
                if (c.getPrecision() != null) {
                    sql += "(" + c.getPrecision();
                    sql += c.getScale() != null ? "," + c.getScale() : "";
                    sql += ")";
                }
                if(c.isAutoIncrementChanged() && c.isAutoIncrement()) {
                    sql += String.format(" IDENTITY(%s, %s) ", c.getAutoIncrementStartWith(), c.getAutoIncrementStep());
                }
                if(c.isPrimaryKey()) {
                    primaryKeys.add(c.getName());
                }
                sql += c.isUnique() ? " UNIQUE " : "";
                if(c.isNullableChanged()) {
                    sql += c.isNullable() ? "" : " NOT NULL ";
                }
                if(c.isDefaultValueSet()) {
                    sql += " DEFAULT " + getQueryValueFromObject(c.getDefaultValue()) + " ";
                }
                if (i < t.getChanges().size()) {
                    sql += ", ";
                }
            }
        }
        if(!primaryKeys.isEmpty()) {
            sql += " PRIMARY KEY ( ";
            sql += Strings.join(primaryKeys, ',');
            sql+= " )";
        }
        sql += " );";
        toReturn.add(new ImmutablePair<>(sql, null));

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

            sql += ";";

            toReturn.add(new ImmutablePair<>(sql, null));
        }

        return toReturn;
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

    public List<Pair<String, Object[]>> getAlterTableCommand(Table t) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();
        String sql = null;

        for (Column c : t.getChanges()) {
            sql = "";
            if(c.isAutoIncrement() && c.getType() == null && c.getRawType() == null) {
                c.setType(JDBCType.INTEGER);
                c.setTypeChanged(true);
            }

            if (c.getOperationType() == OperationType.create) {
                sql += " ALTER TABLE ";
                sql += t.getName();
                sql += " ADD ";
                sql += c.getName() + " ";
                if(c.getRawType() == null) {
                    if (c.getType().equals(JDBCType.BOOLEAN)) {
                        sql += "BIT ";
                    } else if (c.getType().equals(JDBCType.TIMESTAMP)) {
                        sql += "DATETIME ";
                    } else {
                        sql += c.getType() + " ";
                    }
                } else {
                    sql += c.getRawType() + " ";
                }
                if (c.getPrecision() != null) {
                    sql += "(" + c.getPrecision();
                    sql += c.getScale() != null ? "," + c.getScale() : "";
                    sql += ")";
                }
                if(c.isAutoIncrementChanged() && c.isAutoIncrement()) {
                    sql += String.format(" IDENTITY(%s, %s) ", c.getAutoIncrementStartWith(), c.getAutoIncrementStep());
                }
                sql += c.isPrimaryKey() ? " PRIMARY KEY " : "";
                sql += c.isUnique() ? " UNIQUE " : "";
                if(c.isNullableChanged()) {
                    sql += c.isNullable() ? "" : " NOT NULL ";
                }
            } else if (c.getOperationType() == OperationType.alter) {
                sql += " ALTER TABLE ";
                sql += t.getName();
                sql += " ALTER COLUMN ";
                sql += c.getName() + " ";
                if(c.getRawType() == null) {
                    if (c.getType().equals(JDBCType.BOOLEAN)) {
                        sql += "BIT ";
                    } else if (c.getType().equals(JDBCType.TIMESTAMP)) {
                        sql += "DATETIME ";
                    } else {
                        sql += c.getType() + " ";
                    }
                } else {
                    sql += c.getRawType() + " ";
                }
                if (c.getPrecision() != null) {
                    sql += "(" + c.getPrecision();
                    sql += c.getScale() != null ? "," + c.getScale() : "";
                    sql += ")";
                }
                if(c.isAutoIncrementChanged() && c.isAutoIncrement()) {
                    throw new JFException("SQLServer does not permit to alter a column adding am identity to it.");
                }
                sql += c.isPrimaryKey() ? " PRIMARY KEY " : "";
                sql += c.isUnique() ? " UNIQUE " : "";
                if(c.isNullableChanged()) {
                    sql += c.isNullable() ? "" : " NOT NULL ";
                }
            }

            sql += ";";
            toReturn.add(new ImmutablePair<>(sql, null));

            if(c.isDefaultValueSet()) {
                sql = String.format(" ALTER TABLE %s ADD CONSTRAINT %s_%s_def DEFAULT %s FOR %s;", t.getName(), t.getName(), c.getName(), getQueryValueFromObject(c.getDefaultValue()), c.getName());
                toReturn.add(new ImmutablePair<>(sql, null));
            }
        }

        for (ForeignKey k : t.getAddedForeignKeys()) {
            sql = "";
            sql += "ALTER TABLE " + k.getFromTable() + " ";
            sql += "ADD CONSTRAINT " + k.getName() + " FOREIGN KEY ( ";
            for (int i = 0; i < k.getForeignColumns().size(); i++) {
                String c = k.getForeignColumns().get(i);
                sql += " " + c;
                if (i < k.getForeignColumns().size() - 1) {
                    sql += ", ";
                }
            }
            sql += " ) ";
            sql += "    REFERENCES " + k.getToTable() + " ( ";
            for (int i = 0; i < k.getPrimaryKeys().size(); i++) {
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

            sql += ";";

            toReturn.add(new ImmutablePair<>(sql, null));
        }

        return toReturn;
    }

    public List<Pair<String, Object[]>> getUpdateCommand(Data d) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();

        for (int i = 0; i < d.getData().size(); i++) {
            Map<String, Object> m = d.getData().get(i);

            String sql = "";
            List<Object> values = new ArrayList<>();

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
            if (!d.isAllRows()) {
                sql += " WHERE 1 = 1 ";
                for (Map<String, Object> w : d.getWhere()) {
                    for (String k : w.keySet()) {
                        sql += " AND " + k + " = ? ";
                        values.add(w.get(k));
                    }
                }
            }

            sql += ";";

            toReturn.add(new ImmutablePair<>(sql, values.toArray()));
        }

        return toReturn;
    }
}

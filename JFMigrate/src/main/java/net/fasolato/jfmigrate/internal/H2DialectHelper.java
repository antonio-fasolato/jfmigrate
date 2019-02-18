package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.*;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class H2DialectHelper extends GenericDialectHelper implements IDialectHelper {
    public String getDatabaseVersionTableExistenceCommand() {
        String sql = "";

        sql += " SELECT COUNT(*) AS count  ";
        sql += " FROM information_schema.tables  ";
        sql += " WHERE 1 = 1  ";
        sql += "   and table_name = '" + JFMigrationConstants.DB_VERSION_TABLE_NAME.toUpperCase() + "'; ";

        return sql;
    }

    public String getDatabaseVersionCommand() {
        String sql = "";

        sql += " select ifnull(max(version), 0) ";
        sql += " from " + JFMigrationConstants.DB_VERSION_TABLE_NAME + "; ";

        return sql;
    }

    public String getSearchDatabaseVersionCommand() {
        String sql = "";

        sql += " select version  ";
        sql += " from " + JFMigrationConstants.DB_VERSION_TABLE_NAME;
        sql += " where 1 = 1 ";
        sql += " 	and version = ? ";
        sql += ";";

        return sql;
    }

    public String getVersionTableCreationCommand() {
        String sql = "";

        sql += " create table " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ( ";
        sql += "   version bigint primary key, ";
        sql += "   appliedat timestamp not null, ";
        sql += "   migrationname varchar(255) not null ";
        sql += " ); ";

        return sql;
    }

    public String getInsertNewVersionCommand() {
        String sql = "";

        sql += "insert into " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";
        sql += "	(version, appliedat, migrationname)";
        sql += "values";
        sql += "	(?, CURRENT_TIMESTAMP(), ?)";
        sql += "; ";

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
        return new String[0];
    }

    public String[] getScriptCheckMigrationDownVersionCommand() {
        return new String[0];
    }

    public List<Pair<String, Object[]>> getTableCreationCommand(Table t) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();
        String sql = "";
        List<Object> values = new ArrayList<>();
        List<String> primaryKeys = new ArrayList<>();

        String postSql = null;
        sql += " CREATE TABLE ";
        sql += t.getName();
        sql += " ( ";
        int i = 0;
        for (Column c : t.getChanges()) {
            i++;
            if (c.getOperationType() == OperationType.create) {
                if(c.isAutoIncrementChanged() && c.getType() == null) {
                    c.setTypeChanged(true);
                    c.setType(JDBCType.INTEGER);
                }
                sql += c.getName() + " " + c.getType() + " ";
                if (c.getPrecision() != null) {
                    sql += "(" + c.getPrecision();
                    sql += c.getScale() != null ? "," + c.getScale() : "";
                    sql += ")";
                }
                if(c.isAutoIncrementChanged() && c.isAutoIncrement()) {
                    sql += " auto_increment ";
                    if(c.getAutoIncrementStartWith() != 1 || c.getAutoIncrementStep() != 1) {
                        postSql = String.format(" ALTER TABLE %s ALTER COLUMN %s RESTART WITH %s ", t.getName(), c.getName(), c.getAutoIncrementStartWith());
                    }
                }
                if(c.isPrimaryKey()) {
                    primaryKeys.add(c.getName());
                }
                sql += c.isUnique() ? " UNIQUE " : "";
                sql += c.isNullable() ? "" : " NOT NULL ";
                if(c.isDefaultValueSet()) {
                    sql += " DEFAULT ? ";
                    values.add(c.getDefaultValue());
                }
                if (i < t.getChanges().size()) {
                    sql += ", ";
                }
            }
        }
        if(!primaryKeys.isEmpty()) {
            sql += String.format(" , PRIMARY KEY(%s)", String.join(",", primaryKeys));
        }
        sql += " );";
        toReturn.add(new Pair<>(sql, values.isEmpty() ? null : values.toArray()));
        if(postSql != null) {
            toReturn.add(new Pair<>(postSql, null));
        }

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

            toReturn.add(new Pair<>(sql, null));
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

        sql += " DROP INDEX " + i.getName() + " ;";

        return new String[]{sql};
    }

    public String[] getColumnDropCommand(Column c) {
        String sql = "";

        sql += " ALTER TABLE " + c.getTableName() + " DROP COLUMN " + c.getName() + " ;";

        return new String[]{sql};
    }

    public String[] getTableRenameCommand(Table t) {
        String sql = "";

        sql += " ALTER TABLE " + t.getName() + " RENAME TO " + t.getNewName() + " ;";

        return new String[]{sql};
    }

    public String[] getColumnRenameCommand(Column c) {
        String sql = "";

        sql += " ALTER TABLE " + c.getTableName() + " ALTER COLUMN " + c.getName() + " RENAME TO " + c.getNewName() + " ;";

        return new String[]{sql};
    }

    public List<Pair<String, Object[]>> getAlterTableCommand(Table t) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();
        List<String> primaryKeys = new ArrayList<>();

        for (Column c : t.getChanges()) {
            if(c.isAutoIncrementChanged() && c.getType() == null) {
                c.setTypeChanged(true);
                c.setType(JDBCType.INTEGER);
            }
            String sql = "";
            String postSql = null;
            List<Object> values = new ArrayList<>();
            if (c.getOperationType() == OperationType.create) {
                sql += " ALTER TABLE ";
                sql += t.getName();
                sql += " ADD COLUMN ";
                sql += c.getName() + " " + c.getType() + " ";
                if (c.getPrecision() != null) {
                    sql += "(" + c.getPrecision();
                    sql += c.getScale() != null ? "," + c.getScale() : "";
                    sql += ")";
                }
                if(c.isAutoIncrementChanged() && c.isAutoIncrement()) {
                    sql += " auto_increment ";
                    if(c.getAutoIncrementStartWith() != 1 || c.getAutoIncrementStep() != 1) {
                        postSql = String.format(" ALTER TABLE %s ALTER COLUMN %s RESTART WITH %s ", t.getName(), c.getName(), c.getAutoIncrementStartWith());
                    }
                }
                if(c.isPrimaryKey()) {
                    primaryKeys.add(c.getName());
                }
                sql += c.isUnique() ? " UNIQUE " : "";
                sql += c.isNullable() ? "" : " NOT NULL ";
                if(c.isDefaultValueSet()) {
                    sql += " DEFAULT ? ";
                    values.add(c.getDefaultValue());
                }
            } else if (c.getOperationType() == OperationType.alter) {
                sql += " ALTER TABLE ";
                sql += t.getName();
                sql += " ALTER COLUMN ";
                sql += c.getName() + " " + c.getType() + " ";
                if (c.getPrecision() != null) {
                    sql += "(" + c.getPrecision();
                    sql += c.getScale() != null ? "," + c.getScale() : "";
                    sql += ")";
                }
                if(c.isAutoIncrementChanged() && c.isAutoIncrement()) {
                    sql += " auto_increment ";
                    if(c.getAutoIncrementStartWith() != 1 || c.getAutoIncrementStep() != 1) {
                        postSql = String.format(" ALTER TABLE %s ALTER COLUMN %s RESTART WITH %s ", t.getName(), c.getName(), c.getAutoIncrementStartWith());
                    }
                }
                if(c.isPrimaryKey()) {
                    primaryKeys.add(c.getName());
                }
                sql += c.isUnique() ? " UNIQUE " : "";
                sql += c.isNullable() ? "" : " NOT NULL ";
                if(c.isDefaultValueSet()) {
                    sql += " DEFAULT ? ";
                    values.add(c.getDefaultValue());
                }
            }
            if(!primaryKeys.isEmpty()) {
                sql += String.format(" , PRIMARY KEY(%s)", String.join(",", primaryKeys));
            }
            sql += ";";
            toReturn.add(new Pair<>(sql, values.isEmpty() ? null : values.toArray()));
            if(postSql != null) {
                toReturn.add(new Pair<>(postSql, null));
            }
        }

        return toReturn;
    }

    @Override
    public List<Pair<String, Object[]>> getDeleteCommand(Data d) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<Pair<String, Object[]>>();

        if (!d.isAllRows()) {
            for (Map<String, Object> w : d.getWhere()) {
                String sql = "";
                List<Object> values = new ArrayList<Object>();

                sql += " DELETE " + d.getTableName() + " WHERE 1 = 1 ";
                for (String k : w.keySet()) {
                    sql += " AND " + k + " = ? ";
                    values.add(w.get(k));
                }
                sql += ";";

                toReturn.add(new Pair<String, Object[]>(sql, values.toArray()));
            }
        } else {
            String sql = " DELETE " + d.getTableName() + "; ";

            toReturn.add(new Pair<String, Object[]>(sql, new Object[0]));
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

            toReturn.add(new Pair<String, Object[]>(sql, values.toArray()));
        }

        return toReturn;
    }
}

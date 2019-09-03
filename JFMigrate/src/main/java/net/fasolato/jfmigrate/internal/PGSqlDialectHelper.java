package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PGSqlDialectHelper extends GenericDialectHelper implements IDialectHelper {
    public String getDatabaseVersionTableExistenceCommand() {
        String sql = "";

        sql += " SELECT count(*) as count from (  ";
        sql += "  SELECT 1  ";
        sql += "  FROM   pg_catalog.pg_class c  ";
        sql += "  JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace  ";
        sql += "  WHERE  1 = 1  ";
//        sql += "   AND n.nspname = 'schema_name'  ";
        sql += "   AND    c.relname = '" + JFMigrationConstants.DB_VERSION_TABLE_NAME + "'  ";
        sql += " ) a;  ";

        return sql;
    }

    public String getDatabaseVersionCommand() {
            String sql = "";

            sql += " select coalesce(max(version), 0) as version  ";
            sql += " from jfmigratedbversion;  ";

            return sql;
    }

    public String getSearchDatabaseVersionCommand() {
        String sql = "";

        sql += " select version ";
        sql += " from " + JFMigrationConstants.DB_VERSION_TABLE_NAME;
        sql += " where 1 = 1 ";
        sql += " 	and version = ? ";
        sql += ";";

        return sql;
    }

    public String getVersionTableCreationCommand() {
        String sql = "";

        sql += " create table jfmigratedbversion (  ";
        sql += "   version bigint primary key,  ";
        sql += "   appliedat timestamp not null,  ";
        sql += "   migrationname varchar(255) not null  ";
        sql += " );  ";

        return sql;
    }

    public String getInsertNewVersionCommand() {
        String sql = "";

        sql += "insert into " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";
        sql += "	(version, appliedat, migrationname)";
        sql += "values";
        sql += "	(?, current_timestamp, ?);";

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

        sql += " DO  \n";
        sql += " $do$  \n";
        sql += " BEGIN  \n";
        sql += " 	if not exists (select * from jfmigratedbversion where version = ?) then  \n";
        toReturn.add(sql);

        sql = "";
        sql += "     end if;  \n";
        sql += " END  \n";
        sql += " $do$;  \n";
        toReturn.add(sql);

        return toReturn.toArray(new String[toReturn.size()]);
    }

    public String[] getScriptCheckMigrationDownVersionCommand() {
        List<String> toReturn = new ArrayList<String>();
        String sql = "";

        sql += " DO  \n";
        sql += " $do$  \n";
        sql += " BEGIN  \n";
        sql += " 	if exists (select * from jfmigratedbversion where version = ?) then  \n";
        toReturn.add(sql);

        sql = "";
        sql += "     end if;  \n";
        sql += " END  \n";
        sql += " $do$;  \n";
        toReturn.add(sql);

        return toReturn.toArray(new String[toReturn.size()]);
    }

    public List<Pair<String, Object[]>> getTableCreationCommand(Table t) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();
        String sql = "";

        sql += " CREATE TABLE ";
        sql += t.getName();
        sql += " ( ";
        int i = 0;
        List<String> pks = new ArrayList<>();
        for (Column c : t.getChanges()) {
            i++;
            if (c.getOperationType() == OperationType.create) {
                sql += c.getName() + " ";
                if (!c.isAutoIncrement()) {
                    sql += c.getRawType() == null ? c.getType() : c.getRawType() + " ";
                    if (c.getPrecision() != null) {
                        sql += "(" + c.getPrecision();
                        sql += c.getScale() != null ? "," + c.getScale() : "";
                        sql += ")";
                    }
                } else {
                    String sequenceName = String.format("seq_%s_%s", t.getName(), RandomStringUtils.random(8, "0123456789abcdef"));
                    String preSql;
                    if(c.getAutoIncrementStartWith() == 1) {
                        preSql = String.format(" CREATE SEQUENCE %s ", sequenceName);
                    } else {
                        preSql = String.format(" CREATE SEQUENCE %s START WITH %s ", sequenceName, c.getAutoIncrementStartWith());
                    }
                    if(c.getAutoIncrementStep() != 1) {
                        preSql += String.format("INCREMENT BY %s ", c.getAutoIncrementStep());
                    }
                    preSql += ";";
                    toReturn.add(new ImmutablePair<>(preSql, null));

                    sql += String.format(" int DEFAULT nextval('%s') ", sequenceName);
                }
                sql += c.isUnique() ? " UNIQUE " : "";
                if(c.isNullableChanged()) {
                    sql += c.isNullable() ? "" : " NOT NULL ";
                }
                if (c.isDefaultValueSet()) {
                    sql += " DEFAULT " + getQueryValueFromObject(c.getDefaultValue()) + " ";
                }
                if (i < t.getChanges().size()) {
                    sql += ", ";
                }
                if(c.isPrimaryKey()) {
                    pks.add(c.getName());
                }
            }
        }
        if(!pks.isEmpty()) {
            sql += String.format(" ,PRIMARY KEY(%s)", Strings.join(pks, ','));
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

        sql += " ALTER TABLE " + c.getTableName() + " RENAME COLUMN " + c.getName() + " TO " + c.getNewName() + " ;";

        return new String[]{sql};
    }

    public List<Pair<String, Object[]>> getAlterTableCommand(Table t) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();

        for (Column c : t.getChanges()) {
            String sql = "";
            if (c.getOperationType() == OperationType.create) {
                sql += " ALTER TABLE ";
                sql += t.getName();
                sql += " ADD COLUMN ";
                sql += c.getName() + " ";
                if (c.isAutoIncrement()) {
                    String sequenceName = String.format("seq_%s_%s", t.getName(), RandomStringUtils.random(8, "0123456789abcdef"));
                    String preSql;
                    if(c.getAutoIncrementStartWith() == 1) {
                        preSql = String.format(" CREATE SEQUENCE %s ", sequenceName);
                    } else {
                        preSql = String.format(" CREATE SEQUENCE %s START WITH %s ", sequenceName, c.getAutoIncrementStartWith());
                    }
                    if(c.getAutoIncrementStep() != 1) {
                        preSql += String.format("INCREMENT BY %s ", c.getAutoIncrementStep());
                    }
                    preSql += ";";
                    toReturn.add(new ImmutablePair<>(preSql, null));

                    sql += String.format(" int DEFAULT nextval('%s') ", sequenceName);
                } else {
                    sql += c.getRawType() == null ? c.getType() : c.getRawType() + " ";
                    if (c.getPrecision() != null) {
                        sql += "(" + c.getPrecision();
                        sql += c.getScale() != null ? "," + c.getScale() : "";
                        sql += ")";
                    }
                }
                sql += c.isPrimaryKey() ? " PRIMARY KEY " : "";
                sql += c.isUnique() ? " UNIQUE " : "";
                if(c.isNullableChanged()) {
                    sql += c.isNullable() ? "" : " NOT NULL ";
                }
                if (c.isDefaultValueSet()) {
                    sql += " DEFAULT " + getQueryValueFromObject(c.getDefaultValue()) + " ";
                }
                toReturn.add(new ImmutablePair<>(sql, null));
            } else if (c.getOperationType() == OperationType.alter) {
                if (c.isTypeChanged() && c.isAutoIncrement()) {
                    String sequenceName = String.format("seq_%s_%s", t.getName(), RandomStringUtils.random(8, "0123456789abcdef"));
                    if(c.getAutoIncrementStartWith() == 1) {
                        sql = String.format(" CREATE SEQUENCE %s ", sequenceName);
                    } else {
                        sql = String.format(" CREATE SEQUENCE %s START WITH %s ", sequenceName, c.getAutoIncrementStartWith());
                    }
                    if(c.getAutoIncrementStep() != 1) {
                        sql += String.format("INCREMENT BY %s ", c.getAutoIncrementStep());
                    }
                    sql += ";";
                    toReturn.add(new ImmutablePair<>(sql, null));

                    sql = String.format(" ALTER TABLE %s ALTER COLUMN %s SET DEFAULT nextval('%s'); ", t.getName(), c.getName(), sequenceName);
                    toReturn.add(new ImmutablePair<>(sql, null));
                } else if (c.isTypeChanged()) {
                    sql = "";
                    sql += " ALTER TABLE ";
                    sql += t.getName();
                    sql += " ALTER COLUMN ";
                    sql += c.getName() + " TYPE ";
                    sql += c.getRawType() == null ? c.getType() : c.getRawType() + " ";
                    if (c.getPrecision() != null) {
                        sql += "(" + c.getPrecision();
                        sql += c.getScale() != null ? "," + c.getScale() : "";
                        sql += ")";
                    }
                    sql += c.isPrimaryKey() ? " PRIMARY KEY " : "";
                    sql += c.isUnique() ? " UNIQUE " : "";
                    sql += ";";
                    toReturn.add(new ImmutablePair<>(sql, null));
                }

                if (c.isNullableChanged()) {
                    sql = String.format(" ALTER TABLE %s ALTER COLUMN %s %s;", t.getName(), c.getName(), c.isNullable() ? "DROP NOT NULL" : "SET NOT NULL");
                    toReturn.add(new ImmutablePair<>(sql, null));
                }

                if (c.isDefaultValueSet()) {
                    sql = String.format(" ALTER TABLE %s ALTER COLUMN %s SET DEFAULT %s; ", t.getName(), c.getName(), getQueryValueFromObject(c.getDefaultValue()));
                    toReturn.add(new ImmutablePair<>(sql, null));
                }
            }
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

            toReturn.add(new ImmutablePair<String, Object[]>(sql, values.toArray()));
        }

        return toReturn;
    }
}

package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.JFException;
import net.fasolato.jfmigrate.builders.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

public class SqliteDialectHelper extends GenericDialectHelper implements IDialectHelper{
    private static Logger log = LogManager.getLogger(SqliteDialectHelper.class);

    @Override
    public String getDatabaseVersionTableExistenceCommand() {
        String sql = "";

        sql += String.format("select count(*) as count from ( SELECT 1 FROM sqlite_master WHERE type='table' AND name='%s' )", JFMigrationConstants.DB_VERSION_TABLE_NAME);

        return sql;
    }

    @Override
    public String getDatabaseVersionCommand() {
        String sql = "";

        sql += " select coalesce(max(version), 0) as version  ";
        sql += " from jfmigratedbversion;  ";

        return sql;
    }

    @Override
    public String getSearchDatabaseVersionCommand() {
        String sql = "";

        sql += " select version ";
        sql += " from " + JFMigrationConstants.DB_VERSION_TABLE_NAME;
        sql += " where 1 = 1 ";
        sql += " 	and version = ? ";
        sql += ";";

        return sql;
    }

    @Override
    public String getVersionTableCreationCommand() {
        String sql = "";

        sql += " create table jfmigratedbversion (  ";
        sql += "   version bigint primary key,  ";
        sql += "   appliedat timestamp not null,  ";
        sql += "   migrationname varchar(255) not null  ";
        sql += " );  ";

        return sql;
    }

    @Override
    public String getInsertNewVersionCommand() {
        String sql = "";

        sql += "insert into " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";
        sql += "	(version, appliedat, migrationname)";
        sql += "values";
        sql += "	(?, current_timestamp, ?);";

        return sql;
    }

    @Override
    public String getDeleteVersionCommand() {
        String sql = "";

        sql += " delete from " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";
        sql += " where 1 = 1 ";
        sql += "	and version = ? ";
        sql += ";";

        return sql;
    }

    @Override
    public String[] getScriptCheckMigrationUpVersionCommand() {
        return new String[0];
    }

    @Override
    public String[] getScriptCheckMigrationDownVersionCommand() {
        return new String[0];
    }

    @Override
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
                sql += c.getRawType() == null ?  c.getType() : c.getRawType() + " ";
                if (c.getPrecision() != null) {
                    sql += "(" + c.getPrecision();
                    sql += c.getScale() != null ? "," + c.getScale() : "";
                    sql += ")";
                }
                if (c.isAutoIncrement()) {
                    sql += " AUTOINCREMENT ";
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

        for (ForeignKey k : t.getAddedForeignKeys()) {
            sql += " CONSTRAINT " + k.getName() + " FOREIGN KEY ( ";
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
        }

        sql += " );";
        toReturn.add(new ImmutablePair<>(sql, null));

        return toReturn;
    }

    @Override
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

    @Override
    public String[] getTableDropCommand(Table t) {
        String sql = "";

        sql += " DROP TABLE " + t.getName() + " ;";

        return new String[]{sql};
    }

    @Override
    public String[] getIndexDropCommand(Index i) {
        String sql = "";

        sql += String.format(" DROP INDEX %s ;", i.getName());

        return new String[]{sql};
    }

    @Override
    public String[] getColumnDropCommand(Column c) {
        throw new JFException("Sqlite does not support DROP COLUMN");
    }

    @Override
    public String[] getTableRenameCommand(Table t) {
        String sql = "";

        sql += " ALTER TABLE " + t.getName() + " RENAME TO " + t.getNewName() + " ;";

        return new String[]{sql};
    }

    @Override
    public String[] getColumnRenameCommand(Column c) {
        String sql = "";

        sql += " ALTER TABLE " + c.getTableName() + " RENAME COLUMN " + c.getName() + " TO " + c.getNewName() + " ;";

        return new String[]{sql};
    }

    @Override
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
                    sql += c.getRawType() == null ?  c.getType() : c.getRawType() + " ";
                    if (c.getPrecision() != null) {
                        sql += "(" + c.getPrecision();
                        sql += c.getScale() != null ? "," + c.getScale() : "";
                        sql += ")";
                    }
                }
                if(c.isPrimaryKey()) {
                    throw new JFException("Sqlite does not support adding a primary key column");
                }
                sql += c.isUnique() ? " UNIQUE " : "";
                if(c.isNullableChanged()) {
                    sql += c.isNullable() ? "" : " NOT NULL ";
                }
                if (c.isDefaultValueSet()) {
                    sql += " DEFAULT " + getQueryValueFromObject(c.getDefaultValue()) + " ";
                }

                if(!t.getAddedForeignKeys().isEmpty()) {
                    throw new JFException("Sqlite does not support ALTER TABLE ... ADD CONSTRAINT...");
                }

                log.warn("Sqlite does not support DROP COLUMN, so this will be a non reversible migration");

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
                    sql += c.getRawType() == null ?  c.getType() : c.getRawType() + " ";
                    if (c.getPrecision() != null) {
                        sql += "(" + c.getPrecision();
                        sql += c.getScale() != null ? "," + c.getScale() : "";
                        sql += ")";
                    }
                    if(c.isPrimaryKey()) {
                        throw new JFException("Sqlite does not support setting a column as primary");
                    }
                    sql += c.isUnique() ? " UNIQUE " : "";
                    sql += ";";

                    if(!t.getAddedForeignKeys().isEmpty()) {
                        throw new JFException("Sqlite does not support ALTER TABLE ... ADD CONSTRAINT...");
                    }

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
}

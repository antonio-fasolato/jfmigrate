package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.JFException;
import net.fasolato.jfmigrate.builders.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.JDBCType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OracleDialectHelper extends GenericDialectHelper implements IDialectHelper {
    private static Logger log = LogManager.getLogger(OracleDialectHelper.class);

    public OracleDialectHelper() {
        querySeparator = "";
    }

    @Override
    public String getDatabaseVersionTableExistenceCommand() {
        return String.format(" SELECT count(*) + 1 from \"%s\" ", JFMigrationConstants.DB_VERSION_TABLE_NAME);
    }

    @Override
    public String getDatabaseVersionCommand() {
        return String.format(" select coalesce(max(\"version\"), 0) as \"version\" from \"%s\" ", JFMigrationConstants.DB_VERSION_TABLE_NAME);
    }

    @Override
    public String getSearchDatabaseVersionCommand() {
        return String.format(" select * from \"%s\" where \"version\" = ? ", JFMigrationConstants.DB_VERSION_TABLE_NAME);
    }

    @Override
    public String getVersionTableCreationCommand() {
        return String.format(" CREATE TABLE \"%s\" (\"version\" INTEGER primary key, \"appliedat\" timestamp not null, \"migrationname\" VARCHAR2(255) not null) ", JFMigrationConstants.DB_VERSION_TABLE_NAME);
    }

    @Override
    public String getVersionTableDeleteCommand() {
        return String.format(" DROP TABLE %s; ", JFMigrationConstants.DB_VERSION_TABLE_NAME);
    }

    @Override
    public String getInsertNewVersionCommand() {
        return String.format("insert into \"%s\" (\"version\", \"appliedat\", \"migrationname\") values (?, CURRENT_TIMESTAMP, ?)", JFMigrationConstants.DB_VERSION_TABLE_NAME);
    }

    @Override
    public String getDeleteVersionCommand() {
        return String.format(" DELETE FROM \"%s\" WHERE \"version\" = ? ", JFMigrationConstants.DB_VERSION_TABLE_NAME);
    }

    // TODO Implement
    @Override
    public String[] getScriptCheckMigrationUpVersionCommand() {
        return null;
    }

    // TODO Implement
    @Override
    public String[] getScriptCheckMigrationDownVersionCommand() {
        return null;
    }

    @Override
    public List<Pair<String, Object[]>> getTableCreationCommand(Table t) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();
        String sql = "";

        sql += String.format(" CREATE TABLE \"%s\" ( ", t.getName());
        int i = 0;
        List<String> pks = new ArrayList<>();
        for (Column c : t.getChanges()) {
            if(c.isAutoIncrement()) {
                log.warn("Oracle does not support directly an autoincrement column (not in alla versions). You should manage this manually (sequence or trigger)");
            }
            i++;
            if (c.getOperationType() == OperationType.create) {
                sql += String.format("\"%s\" %s ", c.getName(), c.getRawType() == null ? c.getType() : c.getRawType());
                if (c.getPrecision() != null) {
                    sql += String.format("(%s%s)", c.getPrecision(), c.getScale() != null ? "," + c.getScale() : "");
                }
                if (c.isDefaultValueSet()) {
                    sql += String.format(" DEFAULT %s ", getQueryValueFromObject(c.getDefaultValue()));
                }
                if(c.isPrimaryKey()) {
                    pks.add(c.getName());
                }
                sql += c.isUnique() ? " UNIQUE " : "";
                if(c.isNullableChanged()) {
                    sql += c.isNullable() ? "" : " NOT NULL ";
                }
                if (i < t.getChanges().size()) {
                    sql += ", ";
                }
            }
        }
        if(!pks.isEmpty()) {
            sql += String.format(" , CONSTRAINT \"pk_%s\" PRIMARY KEY (\"%s\")", t.getName(), String.join("\",\"", pks));
        }

        for (ForeignKey k : t.getAddedForeignKeys()) {
            sql += String.format(" , CONSTRAINT \"%s\" ", k.getName());
            sql += String.format(" FOREIGN KEY (\"%s\")", String.join("\",\"", k.getForeignColumns()));
            sql += String.format(" REFERENCES \"%s\" (\"%s\") ", k.getToTable(), String.join("\",\"", k.getPrimaryKeys()));
            if (k.isOnDeleteCascade()) {
                sql += " ON DELETE CASCADE ";
            }
            if (k.isOnUpdateCascade()) {
                throw new JFException("ON UPDATE CASCADE NOT SUPPORTED IN ORACLE");
            }
        }

        sql += " )";
        toReturn.add(new ImmutablePair<>(sql, null));

        return toReturn;
    }

    @Override
    public String[] getIndexCreationCommand(Index i) {
        List<String> toReturn = new ArrayList<String>();

        String sql = String.format(" CREATE %s INDEX \"%s\" ON \"%s\"(\"%s\") ", i.isUnique() ? "UNIQUE" : "", i.getName(), i.getTableName(), String.join("\",\"", i.getColumns()));
        toReturn.add(sql);

        return toReturn.toArray(new String[toReturn.size()]);
    }

    @Override
    public String[] getTableDropCommand(Table t) {
        String sql = String.format(" DROP TABLE \"%s\" ", t.getName());
        return new String[]{sql};
    }

    @Override
    public String[] getIndexDropCommand(Index i) {
        String sql = String.format(" DROP INDEX \"%s\" ", i.getName());
        return new String[]{sql};
    }

    @Override
    public String[] getColumnDropCommand(Column c) {
        String sql = String.format(" ALTER TABLE \"%s\" DROP COLUMN \"%s\" ", c.getTableName(), c.getName());
        return new String[]{sql};
    }

    @Override
    public String[] getTableRenameCommand(Table t) {
        String sql = String.format(" ALTER TABLE \"%s\" RENAME TO \"%s\" ", t.getName(), t.getNewName());
        return new String[]{sql};
    }

    @Override
    public String[] getColumnRenameCommand(Column c) {
        String sql = String.format(" ALTER TABLE \"%s\" RENAME COLUMN \"%s\" TO \"%s\" ", c.getTableName(), c.getName(), c.getNewName());
        return new String[]{sql};
    }

    @Override
    public List<Pair<String, Object[]>> getAlterTableCommand(Table t) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();

        for (Column c : t.getChanges()) {
            List <Object> values = new ArrayList<>();
            String sql = "";
            if (c.getOperationType() == OperationType.create) {
                if(c.isAutoIncrement()) {
                    log.warn("Oracle does not support directly an autoincrement column (not in alla versions). You should manage this manually (sequence or trigger)");
                }
                sql += String.format("ALTER TABLE \"%s\" ADD \"%s\" %s ", t.getName(), c.getName(), c.getRawType() == null ? c.getType() : c.getRawType());
                if (c.getPrecision() != null) {
                    sql += String.format(" (%s%s) ", c.getPrecision(), c.getScale() != null ? "," + c.getScale() : "");
                }
                sql += c.isPrimaryKey() ? " PRIMARY KEY " : "";
                sql += c.isUnique() ? " UNIQUE " : "";
                if(c.isDefaultValueSet()) {
                    if(c.getType() == JDBCType.TIMESTAMP) {
                        // https://stackoverflow.com/questions/25489002/why-cannot-i-use-bind-variables-in-ddl-scl-statements-in-dynamic-sql
                        // Oracle does not support query parameters in DDLs
                        SimpleDateFormat sd = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                        sql += String.format(" DEFAULT TO_TIMESTAMP('%s', 'YYYY-MM-DD HH24:MI:SS') ", sd.format(c.getDefaultValue()));
                    } else {
                        sql += " DEFAULT ? ";
                        values.add(c.getDefaultValue());
                    }
                }
                if(c.isNullableChanged()) {
                    sql += c.isNullable() ? "" : " NOT NULL ";
                }
                toReturn.add(new ImmutablePair<>(sql, values.isEmpty() ? null : values.toArray()));
            } else if (c.getOperationType() == OperationType.alter) {
                if(c.isTypeChanged()) {
                    sql = String.format("ALTER TABLE \"%s\" MODIFY (\"%s\" %s", t.getName(), c.getName(), c.getRawType() == null ? c.getType() : c.getRawType());
                    if (c.getPrecision() != null) {
                        sql += String.format("(%s%s)", c.getPrecision(), c.getScale() != null ? "," + c.getScale() : "");
                    }
                    sql += c.isPrimaryKey() ? " PRIMARY KEY " : "";
                    sql += c.isUnique() ? " UNIQUE " : "";
                    if(c.isNullableChanged()) {
                        sql += " " + (c.isNullable() ? "NULL " : "NOT NULL ");
                    }
                    sql += ")";
                    toReturn.add(new ImmutablePair<>(sql, null));
                }

                if(c.isDefaultValueSet()) {
                    sql = String.format("ALTER TABLE \"%s\" MODIFY (\"%s\" ", t.getName(), c.getName());
                    if(c.getDefaultValue() != null) {
                        sql += "SET DEFAULT ?) ";
                        toReturn.add(new ImmutablePair<>(sql, new Object[]{c.getDefaultValue()}));
                    } else {
                        sql += "DROP DEFAULT";
                        toReturn.add(new ImmutablePair<>(sql, null));
                    }
                }
            }
        }

        return toReturn;

    }

    @Override
    public List<Pair<String, Object[]>> getUpdateCommand(Data d) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();

        for (int i = 0; i < d.getData().size(); i++) {
            Map<String, Object> m = d.getData().get(i);

            String sql = "";
            List<Object> values = new ArrayList<>();

            sql += " UPDATE \"" + d.getTableName() + "\" SET ";
            int j = 0;
            for (String k : m.keySet()) {
                sql += "\"" + k + "\" = ? ";
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
                        sql += " AND \"" + k + "\" = ? ";
                        values.add(w.get(k));
                    }
                }
            }

            toReturn.add(new ImmutablePair<>(sql, values.toArray()));
        }

        return toReturn;
    }

    @Override
    public List<Pair<String, Object[]>> getInsertCommand(Data d) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();

        for (Map<String, Object> m : d.getData()) {
            String sql = "";
            List<Object> values = new ArrayList<>();
            for(String k : m.keySet()) {
                values.add(m.get(k));
            }

            sql += String.format(" INSERT INTO \"%s\" (\"%s\") VALUES (%s)%s ", d.getTableName(), String.join("\",\"", m.keySet()), String.join(",", Collections.nCopies(m.keySet().size(), "?")), querySeparator);

            toReturn.add(new ImmutablePair<>(sql, values.toArray()));
        }

        return toReturn;
    }

    @Override
    public List<Pair<String, Object[]>> getDeleteCommand(Data d) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();

        if (!d.isAllRows()) {
            for (Map<String, Object> w : d.getWhere()) {
                List<Object> values = new ArrayList<>();
                String sql = String.format(" DELETE FROM \"%s\" WHERE 1 = 1 ", d.getTableName());
                for (String k : w.keySet()) {
                    sql += String.format(" AND \"%s\" = ?", k);
                    values.add(w.get(k));
                }
                sql += querySeparator;

                toReturn.add(new ImmutablePair<>(sql, values.toArray()));
            }
        } else {
            String sql = String.format(" DELETE FROM \"%s\"%s ", d.getTableName(), querySeparator);

            toReturn.add(new ImmutablePair<>(sql, null));
        }

        return toReturn;
    }

}

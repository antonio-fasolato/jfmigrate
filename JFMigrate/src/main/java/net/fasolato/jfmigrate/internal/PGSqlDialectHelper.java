package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PGSqlDialectHelper implements IDialectHelper {
    public String getDatabaseVersionTableExistenceCommand() {
        String sql = "";

        sql += " SELECT count(*) as count from (  ";
        sql += "  SELECT 1  ";
        sql += "  FROM   pg_catalog.pg_class c  ";
        sql += "  JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace  ";
        sql += "  WHERE  1 = 1  ";
//        sql += "   AND n.nspname = 'schema_name'  ";
        sql += "   AND    c.relname = '" + JFMigrationConstants.DB_VERSION_TABLE_NAME + "'  ";
        sql += " ) a  ";

        return sql;
    }

    public String getDatabaseVersionCommand() {
        String sql = "";

        sql += " select coalesce(max(version), 0) as version  ";
        sql += " from jfmigratedbversion  ";

        return sql;
    }

    public String getSearchDatabaseVersionCommand() {
        String sql = "";

        sql += " select version ";
        sql += " from " + JFMigrationConstants.DB_VERSION_TABLE_NAME;
        sql += " where 1 = 1 ";
        sql += " 	and version = ? ";

        return sql;
    }

    public String getVersionTableCreationCommand() {
        String sql = "";

        sql += " create table jfmigratedbversion (  ";
        sql += "   version bigint primary key,  ";
        sql += "   appliedat timestamp not null,  ";
        sql += "   migrationname varchar(255) not null  ";
        sql += " )  ";

        return sql;
    }

    public String getInsertNewVersionCommand() {
        String sql = "";

        sql += "insert into " + JFMigrationConstants.DB_VERSION_TABLE_NAME + " ";
        sql += "	(version, appliedat, migrationname)";
        sql += "values";
        sql += "	(?, current_timestamp, ?)";

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

    public String[] getAlterTableCommand(Table t) {
        List<String> toReturn = new ArrayList<String>();

        for (Column c : t.getChanges()) {
            String sql = "";
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
                sql += c.isPrimaryKey() ? " PRIMARY KEY " : "";
                sql += c.isUnique() ? " UNIQUE " : "";
                sql += c.isNullable() ? "" : " NOT NULL ";
            } else if (c.getOperationType() == OperationType.alter) {
                sql += " ALTER TABLE ";
                sql += t.getName();
                sql += " ALTER COLUMN ";
                sql += c.getName() + " TYPE " + c.getType() + " ";
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

    public Map.Entry<String[], Object[]> getInsertCommand(Data d) {
        String sql = "";
        List<Object> values = new ArrayList<Object>();

        sql += " INSERT INTO " + d.getTableName() + " (";
        int i = 0;
        for (String k : d.getData().keySet()) {
            sql += k;
            if (i < d.getData().keySet().size() - 1) {
                sql += ", ";
            }
            i++;
        }
        sql += " ) VALUES (";
        i = 0;
        for (String k : d.getData().keySet()) {
            sql += "?";
            values.add(d.getData().get(k));
            if (i < d.getData().keySet().size() - 1) {
                sql += ", ";
            }
            i++;
        }
        sql += " ) ";

        return new AbstractMap.SimpleEntry<String[], Object[]>(new String[]{sql}, values.toArray());
    }
}

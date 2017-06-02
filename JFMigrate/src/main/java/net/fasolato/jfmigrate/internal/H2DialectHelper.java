package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.Column;
import net.fasolato.jfmigrate.builders.Data;
import net.fasolato.jfmigrate.builders.Index;
import net.fasolato.jfmigrate.builders.Table;

import java.util.Map;

public class H2DialectHelper implements IDialectHelper {
    public String getDatabaseVersionTableExistenceCommand() {
        String sql = "";

        sql += " SELECT COUNT(*) AS count  ";
        sql += " FROM information_schema.tables  ";
        sql += " WHERE 1 = 1  ";
        sql += "   and table_name = 'word_types' ";

        return sql;
    }

    public String getDatabaseVersionCommand() {
        String sql = "";

        sql += " select ifnull(max(version), 0) ";
        sql += " from " + JFMigrationConstants.DB_VERSION_TABLE_NAME;

        return sql;
    }

    public String getSearchDatabaseVersionCommand() {
        return null;
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
        return null;
    }

    public String getDeleteVersionCommand() {
        return null;
    }

    public String[] getTableCreationCommand(Table t) {
        return new String[0];
    }

    public String[] getIndexCreationCommand(Index i) {
        return new String[0];
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

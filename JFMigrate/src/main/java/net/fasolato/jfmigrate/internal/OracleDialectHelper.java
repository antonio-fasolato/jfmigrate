package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.Column;
import net.fasolato.jfmigrate.builders.Data;
import net.fasolato.jfmigrate.builders.Index;
import net.fasolato.jfmigrate.builders.Table;

import java.util.List;

public class OracleDialectHelper implements IDialectHelper {
    @Override
    public String getDatabaseVersionTableExistenceCommand() {
        return String.format(" SELECT * from %s WHERE ROWNUM = 1 ", JFMigrationConstants.DB_VERSION_TABLE_NAME);
    }

    @Override
    public String getDatabaseVersionCommand() {
        return null;
    }

    @Override
    public String getSearchDatabaseVersionCommand() {
        return null;
    }

    @Override
    public String getVersionTableCreationCommand() {
        return null;
    }

    @Override
    public String getInsertNewVersionCommand() {
        return null;
    }

    @Override
    public String getDeleteVersionCommand() {
        return null;
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
        return null;
    }

    @Override
    public String[] getIndexCreationCommand(Index i) {
        return new String[0];
    }

    @Override
    public String[] getTableDropCommand(Table t) {
        return new String[0];
    }

    @Override
    public String[] getIndexDropCommand(Index i) {
        return new String[0];
    }

    @Override
    public String[] getColumnDropCommand(Column c) {
        return new String[0];
    }

    @Override
    public String[] getTableRenameCommand(Table t) {
        return new String[0];
    }

    @Override
    public String[] getColumnRenameCommand(Column c) {
        return new String[0];
    }

    @Override
    public List<Pair<String, Object[]>> getAlterTableCommand(Table t) {
        return null;
    }

    @Override
    public List<Pair<String, Object[]>> getInsertCommand(Data d) {
        return null;
    }

    @Override
    public List<Pair<String, Object[]>> getDeleteCommand(Data d) {
        return null;
    }

    @Override
    public List<Pair<String, Object[]>> getUpdateCommand(Data d) {
        return null;
    }
}

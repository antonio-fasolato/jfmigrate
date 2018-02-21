package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.Column;
import net.fasolato.jfmigrate.builders.Data;
import net.fasolato.jfmigrate.builders.Index;
import net.fasolato.jfmigrate.builders.Table;

import java.util.List;

public class MysqlDialectHelper implements IDialectHelper {
    public String getDatabaseVersionTableExistenceCommand() {
        return null;
    }

    public String getDatabaseVersionCommand() {
        return null;
    }

    public String getSearchDatabaseVersionCommand() {
        return null;
    }

    public String getVersionTableCreationCommand() {
        return null;
    }

    public String getInsertNewVersionCommand() {
        return null;
    }

    public String getDeleteVersionCommand() {
        return null;
    }

    public String[] getScriptCheckMigrationUpVersionCommand() {
        return new String[0];
    }

    public String[] getScriptCheckMigrationDownVersionCommand() {
        return new String[0];
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

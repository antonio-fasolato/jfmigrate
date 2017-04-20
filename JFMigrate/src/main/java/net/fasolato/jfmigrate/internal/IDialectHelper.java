package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.Column;
import net.fasolato.jfmigrate.builders.Data;
import net.fasolato.jfmigrate.builders.Index;
import net.fasolato.jfmigrate.builders.Table;

import java.util.Map;

/**
 * Created by fasolato on 21/03/2017.
 */
public interface IDialectHelper {
    String getDatabaseVersionCommand();

    String getSearchDatabaseVersionCommand();

    String getVersionTableCreationCommand();

    String getInsertNewVersionCommand();

    String getDeleteVersionCommand();

    String[] getTableCreationCommand(Table t);

    String[] getIndexCreationCommand(Index i);

    String[] getTableDropCommand(Table t);

    String[] getIndexDropCommand(Index i);

    String[] getColumnDropCommand(Column c);

    String[] getTableRenameCommand(Table t);

    String[] getColumnRenameCommand(Column c);

    String[] getAlterTableCommand(Table t);

    Map.Entry<String[], Object[]> getInsertCommand(Data d);
}

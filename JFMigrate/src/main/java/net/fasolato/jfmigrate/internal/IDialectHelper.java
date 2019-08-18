package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.Column;
import net.fasolato.jfmigrate.builders.Data;
import net.fasolato.jfmigrate.builders.Index;
import net.fasolato.jfmigrate.builders.Table;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by fasolato on 21/03/2017.
 */
public interface IDialectHelper {
    String getDatabaseVersionTableExistenceCommand();

    String getDatabaseVersionCommand();

    String getSearchDatabaseVersionCommand();

    String getVersionTableCreationCommand();

    String getInsertNewVersionCommand();

    String getDeleteVersionCommand();

    String[] getScriptCheckMigrationUpVersionCommand();

    String[] getScriptCheckMigrationDownVersionCommand();

    List<Pair<String, Object[]>> getTableCreationCommand(Table t);

    String[] getIndexCreationCommand(Index i);

    String[] getTableDropCommand(Table t);

    String[] getIndexDropCommand(Index i);

    String[] getColumnDropCommand(Column c);

    String[] getTableRenameCommand(Table t);

    String[] getColumnRenameCommand(Column c);

    List<Pair<String, Object[]>> getAlterTableCommand(Table t);

    List<Pair<String, Object[]>> getInsertCommand(Data d);

    List<Pair<String, Object[]>> getDeleteCommand(Data d);

    List<Pair<String, Object[]>> getUpdateCommand(Data d);
}

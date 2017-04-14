package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.Index;
import net.fasolato.jfmigrate.builders.Table;

/**
 * Created by fasolato on 21/03/2017.
 */
public interface IDialectHelper {
    String getDatabaseVersionCommand();

    String getVersionTableCreationCommand();

    String getInsertNewVersionCommand();

    String[] getTableCreationCommand(Table t);

    String[] getIndexCreationCommand(Index i);

    String tableDropping(String databaseName, String schemaName, Table t);
}

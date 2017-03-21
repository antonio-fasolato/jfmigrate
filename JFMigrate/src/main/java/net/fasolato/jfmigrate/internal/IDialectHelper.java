package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.Table;

/**
 * Created by fasolato on 21/03/2017.
 */
public interface IDialectHelper {
    String tableCreation(String databaseName, String schemaName, Table t);

    String tableDropping(String databaseName, String schemaName, Table t);
}

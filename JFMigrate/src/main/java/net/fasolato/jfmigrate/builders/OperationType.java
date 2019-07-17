package net.fasolato.jfmigrate.builders;

/**
 * Enum to differentiate what current operation we are performing
 */
public enum OperationType {
    /**
     * Create table, index, column
     */
    create,
    /**
     * Alter table, column
     */
    alter,
    /**
     * Rename table, column
     */
    rename,
    /**
     * Drop table, index, column or delete from table
     */
    delete,
    /**
     * Insert into table
     */
    insert,
    /**
     * Update table data
     */
    update
}

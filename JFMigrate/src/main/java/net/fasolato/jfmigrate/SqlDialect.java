package net.fasolato.jfmigrate;

/**
 * Enum to specify all database types managed by JFMigrate
 */
public enum SqlDialect {
    /**
     * Microsoft Sql Server
     */
    SQL_SERVER,
    /**
     * H2 database
     */
    H2,
    /**
     * PostgreSql
     */
    PGSQL,
    /**
     * Mysql and MariaDb
     */
    MYSQL,
    /**
     * Oracle database
     */
    ORACLE,
    /**
     * SqLite
     */
    SQLITE
}

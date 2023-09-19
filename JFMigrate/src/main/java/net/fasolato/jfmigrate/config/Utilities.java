package net.fasolato.jfmigrate.config;

import net.fasolato.jfmigrate.SqlDialect;

public class Utilities {
    public static SqlDialect decodeDialect(String s) {
        SqlDialect dialect = SqlDialect.NONE;
        if (s.equalsIgnoreCase("h2")) {
            dialect = SqlDialect.H2;
        } else if (s.equalsIgnoreCase("sqlserver")) {
            dialect = SqlDialect.SQL_SERVER;
        } else if (s.equalsIgnoreCase("pgsql")) {
            dialect = SqlDialect.PGSQL;
        } else if (s.equalsIgnoreCase("mysql")) {
            dialect = SqlDialect.MYSQL;
        } else if (s.equalsIgnoreCase("oracle")) {
            dialect = SqlDialect.ORACLE;
        } else if (s.equalsIgnoreCase("sqlite")) {
            dialect = SqlDialect.SQLITE;
        }
        return dialect;
    }
}

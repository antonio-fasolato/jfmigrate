package net.fasolato.jfmigrate.config;

import net.fasolato.jfmigrate.SqlDialect;

public class EnvLoader implements ConfigurationLoader {
    @Override
    public SqlDialect getDialect() {
        String var = System.getenv().get("JFMIGRATE_DB_DIALECT");
        SqlDialect dialect = SqlDialect.NONE;
        if (var.equalsIgnoreCase("h2")) {
            dialect = SqlDialect.H2;
        } else if (var.equalsIgnoreCase("sqlserver")) {
            dialect = SqlDialect.SQL_SERVER;
        } else if (var.equalsIgnoreCase("pgsql")) {
            dialect = SqlDialect.PGSQL;
        } else if (var.equalsIgnoreCase("mysql")) {
            dialect = SqlDialect.MYSQL;
        } else if (var.equalsIgnoreCase("oracle")) {
            dialect = SqlDialect.ORACLE;
        } else if (var.equalsIgnoreCase("sqlite")) {
            dialect = SqlDialect.SQLITE;
        }
        return dialect;
    }

    @Override
    public String getScriptLineSeparator() {
        return System.getenv().get("JFMIGRATE_DB_SCRIPT_LINE_SEPARATOR");
    }

    @Override
    public String getConfigUrl() {
        return System.getenv().get("JFMIGRATE_DB_URL");
    }

    @Override
    public String getConfigUsername() {
        return System.getenv().get("JFMIGRATE_DB_USERNAME");
    }

    @Override
    public String getConfigPassword() {
        return System.getenv().get("JFMIGRATE_DB_PASSWORD");
    }

    @Override
    public String getConfigDriverClassName() {
        return System.getenv().get("JFMIGRATE_DB_DRIVERCLASSNAME");
    }
}

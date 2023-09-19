package net.fasolato.jfmigrate.config;

import net.fasolato.jfmigrate.SqlDialect;

public class EnvLoader implements ConfigurationLoader {
    @Override
    public SqlDialect getDialect() {
        String var = System.getenv().get("JFMIGRATE_DB_DIALECT");
        return Utilities.decodeDialect(var);
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

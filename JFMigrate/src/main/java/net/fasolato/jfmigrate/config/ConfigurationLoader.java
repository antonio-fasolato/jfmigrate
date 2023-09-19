package net.fasolato.jfmigrate.config;

import net.fasolato.jfmigrate.SqlDialect;

public interface ConfigurationLoader {
    public SqlDialect getDialect();

    public String getScriptLineSeparator();

    public String getConfigUrl();

    public String getConfigUsername();

    public String getConfigPassword();

    public String getConfigDriverClassName();
}

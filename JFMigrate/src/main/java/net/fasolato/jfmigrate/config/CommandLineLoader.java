package net.fasolato.jfmigrate.config;

import net.fasolato.jfmigrate.SqlDialect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandLineLoader implements ConfigurationLoader {
    private static final Logger log = LogManager.getLogger(CommandLineLoader.class);

    private SqlDialect dialect;
    private String scriptLineSeparator;
    private String configUrl;
    private String configUsername;
    private String configPassword;
    private String configDriverClassName;

    public CommandLineLoader(String[] args) {
        for (String a : args) {
            log.debug(String.format("Found command line argument %s", a));
            String[] pieces = a.split("=");
            if (pieces.length > 1) {
                if (pieces[0].startsWith("--jfmigrate.db.dialect=")) {
                    dialect = Utilities.decodeDialect(pieces[1]);
                }
                if (pieces[0].startsWith("--jfmigrate.db.url=")) {
                    configUrl = pieces[1];
                }
                if (pieces[0].startsWith("--jfmigrate.db.username=")) {
                    configUsername = pieces[1];
                }
                if (pieces[0].startsWith("--jfmigrate.db.password=")) {
                    configPassword = pieces[1];
                }
                if (pieces[0].startsWith("--jfmigrate.db.driverClassName=")) {
                    configDriverClassName = pieces[1];
                }
                if (pieces[0].startsWith("--jfmigrate.db.script_line_separator=")) {
                    scriptLineSeparator = pieces[1];
                }
            }

        }
    }

    @Override
    public SqlDialect getDialect() {
        return dialect;
    }

    @Override
    public String getScriptLineSeparator() {
        return scriptLineSeparator;
    }

    @Override
    public String getConfigUrl() {
        return configUrl;
    }

    @Override
    public String getConfigUsername() {
        return configUsername;
    }

    @Override
    public String getConfigPassword() {
        return configPassword;
    }

    @Override
    public String getConfigDriverClassName() {
        return configDriverClassName;
    }
}

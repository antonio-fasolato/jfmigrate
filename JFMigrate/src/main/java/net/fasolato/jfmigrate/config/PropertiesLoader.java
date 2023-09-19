package net.fasolato.jfmigrate.config;

import net.fasolato.jfmigrate.JFException;
import net.fasolato.jfmigrate.SqlDialect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesLoader implements ConfigurationLoader {
    private static final Logger log = LogManager.getLogger(PropertiesLoader.class);

    private final SqlDialect dialect;
    private final String scriptLineSeparator;
    private final String configUrl;
    private final String configUsername;
    private final String configPassword;
    private final String configDriverClassName;


    public PropertiesLoader() {
        Properties properties = new Properties();
        for (File f : getResourceFolderFiles()) {
            log.info(String.format("Loading properties file %s", f.getName()));
            try {
                properties.load(Files.newInputStream(f.toPath()));
            } catch (Exception e) {
                log.error("Error loading properties file");
                throw new JFException("Error loading properties file", e);
            }
        }

        String configDialect = properties.getProperty("jfmigrate.db.dialect");
        dialect = Utilities.decodeDialect(configDialect);

        scriptLineSeparator = properties.getProperty("jfmigrate.db.script_line_separator");
        configUrl = properties.getProperty("jfmigrate.db.url");
        configUsername = properties.getProperty("jfmigrate.db.username");
        configPassword = properties.getProperty("jfmigrate.db.password");
        configDriverClassName = properties.getProperty("jfmigrate.db.driverClassName");
    }

    private static List<File> getResourceFolderFiles() {
        List<File> toReturn = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("./");
        if (url != null) {
            String path = url.getPath();
            File[] files = new File(path).listFiles();
            if (files != null) {
                for (File f : files) {
                    boolean check = f.isFile() && f.canRead() && f.getName().endsWith(".properties");
                    log.trace(String.format("File %s %s", f.getName(), check ? "is loaded as properties file" : "is skipped as properties file"));
                    if (check) {
                        toReturn.add(f);
                    }
                }
            }
        }
        return toReturn;
    }

    public SqlDialect getDialect() {
        return dialect;
    }

    public String getScriptLineSeparator() {
        return scriptLineSeparator;
    }

    public String getConfigUrl() {
        return configUrl;
    }

    public String getConfigUsername() {
        return configUsername;
    }

    public String getConfigPassword() {
        return configPassword;
    }

    public String getConfigDriverClassName() {
        return configDriverClassName;
    }
}

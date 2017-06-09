package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.JFException;
import net.fasolato.jfmigrate.JFMigrate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by fasolato on 13/04/2017.
 */
public class DatabaseHelper {
    private static Logger log = LogManager.getLogger(DatabaseHelper.class);

    //DB Configuration
    private String configDialect;
    private String configUrl;
    private String configUsername;
    private String configPassword;
    private String configDriverClassName;

    public DatabaseHelper() {
        parseConfiguration();
    }

    public Connection getConnection() {
        try {
            Class.forName(configDriverClassName);
            Connection conn = DriverManager.getConnection(configUrl, configUsername, configPassword);
            return conn;
        } catch (ClassNotFoundException e) {
            log.error(e);
            throw new JFException("Cannot find the Driver class", e);
        } catch (SQLException e) {
            log.error(e);
            throw new JFException("Error connecting to the database", e);
        }
    }

    private void parseConfiguration() {
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("jfmigrate.properties");
        try {
            properties.load(stream);
            configDialect = properties.getProperty("jfmigrate.db.dialect");
            configUrl = properties.getProperty("jfmigrate.db.url");
            configUsername = properties.getProperty("jfmigrate.db.username");
            configPassword = properties.getProperty("jfmigrate.db.password");
            configDriverClassName = properties.getProperty("jfmigrate.db.driverClassName");
            log.info("--- Configuration parameters ---");
            log.info("  dialect: {}", configDialect);
            log.info("  url: {}", configUrl);
            log.info("  username: {}", configUsername);
            log.info("  password: {}", configPassword);
            log.info("  driver: {}", configDriverClassName);
            log.info("--------------------------------");
        } catch (IOException e) {
            log.error(e);
            throw new JFException("Error reading properties file", e);
        }
    }
}

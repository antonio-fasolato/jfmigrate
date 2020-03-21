package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.JFException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    public DatabaseHelper(String configDialect, String configUrl, String configUsername, String configPassword, String configDriverClassName) {
        this.configDialect = configDialect;
        this.configUrl = configUrl;
        this.configUsername = configUsername;
        this.configPassword = configPassword;
        this.configDriverClassName = configDriverClassName;

        log.info("--- Configuration parameters ---");
        log.info("  dialect: {}", this.configDialect);
        log.info("  url: {}", this.configUrl);
        log.info("  username: {}", this.configUsername);
        log.info("  password: {}", this.configPassword);
        log.info("  driver: {}", this.configDriverClassName);
        log.info("--------------------------------");
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
}

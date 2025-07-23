package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    private static String baseConnectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(baseConnectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error: failed to create database", ex);
        }
    }

    static public void configureTables() throws DataAccessException {
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword)) {
            conn.setCatalog(databaseName);  // Ensure correct DB is targeted
            try (var stmt = conn.createStatement()) {

                //users table
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS Users (
                        username VARCHAR(255) PRIMARY KEY,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255)
                    )
                """);

                //AuthTokens table
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS AuthTokens (
                        authToken VARCHAR(255) PRIMARY KEY,
                        username VARCHAR(255),
                        FOREIGN KEY (username) REFERENCES Users(username)
                    )
                """);

                //Games table
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS Games (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        gameName VARCHAR(255),
                        whiteUsername VARCHAR(255),
                        blackUsername VARCHAR(255),
                        game TEXT
                    )
                """);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: failed to configure tables", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            // Attempt to connect using the full connection URL (with DB name)
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            // If the database doesn't exist, try creating it
            if (ex.getMessage().toLowerCase().contains("unknown database")) {
                createDatabase(); // create the DB
                try {
                    var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
                    conn.setCatalog(databaseName);
                    return conn;
                } catch (SQLException retryEx) {
                    throw new DataAccessException("Error: failed to connect after creating DB", retryEx);
                }
            } else {
                throw new DataAccessException("Error: failed to get connection", ex);
            }
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Error: Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("Error: unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        baseConnectionUrl = String.format("jdbc:mysql://%s:%d", host, port);

        connectionUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, databaseName);
    }
}

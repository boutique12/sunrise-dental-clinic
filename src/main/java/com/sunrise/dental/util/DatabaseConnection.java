package com.sunrise.dental.util;

import com.sunrise.dental.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {

    private static DatabaseConnection instance;
    private final String url;
    private final String username;
    private final String password;

    private DatabaseConnection() {
        Properties properties = loadProperties();
        this.url = properties.getProperty("db.url");
        this.username = properties.getProperty("db.username");
        this.password = properties.getProperty("db.password");
        String driver = properties.getProperty("db.driver");

        if (url == null || username == null || password == null || driver == null) {
            throw new DatabaseException("Database configuration is incomplete in db.properties");
        }

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Database driver not found: " + driver, e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to obtain database connection", e);
        }
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new DatabaseException("db.properties not found on classpath");
            }
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new DatabaseException("Failed to load db.properties", e);
        }
    }
}

package com.example.util;

import com.example.exception.DatabaseException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationConfig {
    private static final String CONFIG_FILE = "application.properties";

    private static final String REPOSITORY_TYPE_KEY = "app.repository.type";
    private static final String DRIVER_CLASS_NAME_KEY = "app.datasource.driverClassName";
    private static final String URL_KEY = "app.datasource.url";
    private static final String USERNAME_KEY = "app.datasource.username";
    private static final String PASSWORD_KEY = "app.datasource.password";
    private static final String INITIAL_SIZE_KEY = "app.datasource.pool.initialSize";
    private static final String MAX_SIZE_KEY = "app.datasource.pool.maxSize";
    private static final String CONNECTION_TIMEOUT_KEY = "app.datasource.pool.connectionTimeoutMillis";
    private static final String SCHEMA_LOCATION_KEY = "app.datasource.schemaLocation";

    private static final String DEFAULT_REPOSITORY_TYPE = "cache";
    private static final String DEFAULT_DRIVER = "org.h2.Driver";
    private static final String DEFAULT_URL = "jdbc:h2:./target/quantity-measurement-db;MODE=MySQL;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private static final String DEFAULT_USERNAME = "sa";
    private static final String DEFAULT_PASSWORD = "";
    private static final int DEFAULT_INITIAL_SIZE = 2;
    private static final int DEFAULT_MAX_SIZE = 10;
    private static final long DEFAULT_CONNECTION_TIMEOUT = 5000L;
    private static final String DEFAULT_SCHEMA_LOCATION = "db/schema.sql";

    private final Properties properties;

    private ApplicationConfig(Properties properties) {
        this.properties = properties;
    }

    public static ApplicationConfig load() {
        Properties properties = new Properties();
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException exception) {
            throw new DatabaseException("Unable to load application configuration.", exception);
        }
        return new ApplicationConfig(properties);
    }

    public String getRepositoryType() {
        return getString(REPOSITORY_TYPE_KEY, DEFAULT_REPOSITORY_TYPE);
    }

    public String getDriverClassName() {
        return getString(DRIVER_CLASS_NAME_KEY, DEFAULT_DRIVER);
    }

    public String getDatasourceUrl() {
        return getString(URL_KEY, DEFAULT_URL);
    }

    public String getDatasourceUsername() {
        return getString(USERNAME_KEY, DEFAULT_USERNAME);
    }

    public String getDatasourcePassword() {
        return getString(PASSWORD_KEY, DEFAULT_PASSWORD);
    }

    public int getInitialPoolSize() {
        return getInt(INITIAL_SIZE_KEY, DEFAULT_INITIAL_SIZE);
    }

    public int getMaxPoolSize() {
        return getInt(MAX_SIZE_KEY, DEFAULT_MAX_SIZE);
    }

    public long getConnectionTimeoutMillis() {
        return getLong(CONNECTION_TIMEOUT_KEY, DEFAULT_CONNECTION_TIMEOUT);
    }

    public String getSchemaLocation() {
        return getString(SCHEMA_LOCATION_KEY, DEFAULT_SCHEMA_LOCATION);
    }

    private String getString(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }

        value = properties.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }

        return defaultValue;
    }

    private int getInt(String key, int defaultValue) {
        String value = getString(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new DatabaseException("Invalid integer configuration for key: " + key, exception);
        }
    }

    private long getLong(String key, long defaultValue) {
        String value = getString(key, String.valueOf(defaultValue));
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new DatabaseException("Invalid long configuration for key: " + key, exception);
        }
    }
}

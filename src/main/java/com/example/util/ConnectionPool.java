package com.example.util;

import com.example.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConnectionPool implements AutoCloseable {
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final int maxPoolSize;
    private final long connectionTimeoutMillis;

    private final BlockingQueue<Connection> availableConnections;
    private final Set<Connection> allConnections;

    private volatile boolean closed;

    public ConnectionPool(ApplicationConfig config) {
        this(
                config.getDriverClassName(),
                config.getDatasourceUrl(),
                config.getDatasourceUsername(),
                config.getDatasourcePassword(),
                config.getInitialPoolSize(),
                config.getMaxPoolSize(),
                config.getConnectionTimeoutMillis());
    }

    public ConnectionPool(String driverClassName,
            String jdbcUrl,
            String username,
            String password,
            int initialPoolSize,
            int maxPoolSize,
            long connectionTimeoutMillis) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.connectionTimeoutMillis = connectionTimeoutMillis;

        this.availableConnections = new LinkedBlockingQueue<Connection>();
        this.allConnections = Collections.synchronizedSet(new HashSet<Connection>());

        loadDriver(driverClassName);
        initializePool(initialPoolSize);
    }

    public Connection acquireConnection() {
        ensurePoolOpen();

        Connection pooledConnection = availableConnections.poll();
        if (pooledConnection != null) {
            return pooledConnection;
        }

        synchronized (allConnections) {
            if (allConnections.size() < maxPoolSize) {
                Connection newConnection = createConnection();
                allConnections.add(newConnection);
                return newConnection;
            }
        }

        try {
            pooledConnection = availableConnections.poll(connectionTimeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new DatabaseException("Interrupted while waiting for a database connection.", exception);
        }

        if (pooledConnection == null) {
            throw new DatabaseException("Timed out while waiting for an available database connection.");
        }

        return pooledConnection;
    }

    public void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }

        if (closed) {
            closeQuietly(connection);
            return;
        }

        try {
            if (connection.isClosed()) {
                allConnections.remove(connection);
                return;
            }
        } catch (SQLException exception) {
            allConnections.remove(connection);
            throw new DatabaseException("Unable to validate database connection before release.", exception);
        }

        if (!availableConnections.offer(connection)) {
            closeQuietly(connection);
            allConnections.remove(connection);
        }
    }

    public String getStatistics() {
        int idle = availableConnections.size();
        int total = allConnections.size();
        int active = Math.max(total - idle, 0);
        return String.format("active=%d, idle=%d, total=%d, max=%d", active, idle, total, maxPoolSize);
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;

        synchronized (allConnections) {
            for (Connection connection : allConnections) {
                closeQuietly(connection);
            }
            allConnections.clear();
        }

        availableConnections.clear();
    }

    private void loadDriver(String driverClassName) {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException exception) {
            throw new DatabaseException("Unable to load JDBC driver class: " + driverClassName, exception);
        }
    }

    private void initializePool(int initialPoolSize) {
        int initialSize = Math.max(0, Math.min(initialPoolSize, maxPoolSize));
        for (int index = 0; index < initialSize; index++) {
            Connection connection = createConnection();
            allConnections.add(connection);
            availableConnections.offer(connection);
        }
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to create database connection.", exception);
        }
    }

    private void ensurePoolOpen() {
        if (closed) {
            throw new DatabaseException("Connection pool is closed.");
        }
    }

    private void closeQuietly(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ignored) {
            // Swallowing close exception during cleanup keeps shutdown robust.
        }
    }
}

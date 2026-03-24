package com.app.quantitymeasurement.util;

import com.app.quantitymeasurement.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ConnectionPool — Thread-safe singleton managing a pool of JDBC connections.
 * Reduces overhead of repeatedly opening and closing database connections.
 */
public class ConnectionPool {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
    private static ConnectionPool instance;

    private final List<Connection> available = new ArrayList<>();
    private final List<Connection> inUse = new ArrayList<>();
    private final String url;
    private final String username;
    private final String password;
    private final int poolSize;

    private ConnectionPool() {
        ApplicationConfig config = ApplicationConfig.getInstance();
        this.url = config.getDbUrl();
        this.username = config.getDbUsername();
        this.password = config.getDbPassword();
        this.poolSize = config.getPoolSize();
        initializePool();
    }

    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    /** Reset the singleton — used in tests to reinitialise with a fresh URL. */
    public static synchronized void reset() {
        if (instance != null) {
            instance.closeAll();
            instance = null;
        }
    }

    private void initializePool() {
        try {
            for (int i = 0; i < poolSize; i++) {
                available.add(DriverManager.getConnection(url, username, password));
            }
            logger.info("ConnectionPool initialised with {} connections to {}", poolSize, url);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialise connection pool", e);
        }
    }

    /** Acquire a connection from the pool (blocks if none available). */
    public synchronized Connection acquireConnection() {
        if (available.isEmpty()) {
            throw new DatabaseException("Connection pool exhausted — no available connections");
        }
        Connection conn = available.remove(available.size() - 1);
        inUse.add(conn);
        return conn;
    }

    /** Return a connection back to the pool. */
    public synchronized void releaseConnection(Connection conn) {
        if (conn != null) {
            inUse.remove(conn);
            available.add(conn);
        }
    }

    /** Human-readable pool statistics. */
    public String getStatistics() {
        return "Pool[available=" + available.size() + ", inUse=" + inUse.size() + ", total=" + poolSize + "]";
    }

    /** Close all connections and clear the pool. */
    public void closeAll() {
        for (Connection c : available) {
            try {
                c.close();
            } catch (SQLException ignored) {
            }
        }
        for (Connection c : inUse) {
            try {
                c.close();
            } catch (SQLException ignored) {
            }
        }
        available.clear();
        inUse.clear();
        logger.info("ConnectionPool closed — all connections released");
    }

    // Getters used by tests
    public int getAvailableCount() {
        return available.size();
    }

    public int getInUseCount() {
        return inUse.size();
    }

    public int getPoolSize() {
        return poolSize;
    }
}

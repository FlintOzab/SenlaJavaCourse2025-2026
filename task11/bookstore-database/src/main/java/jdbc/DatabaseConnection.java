package jdbc;

import di.annotation.Component;
import di.annotation.Inject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages database connection for the application.
 * Implements singleton pattern to ensure single connection instance.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class DatabaseConnection {
    
    /** Singleton instance. */
    private static DatabaseConnection instance;
    
    /** Database connection. */
    private Connection connection;
    
    /** Connection properties. */
    private final Properties connectionProperties;
    
    /** PostgreSQL driver class name. */
    private static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
    
    /** Default prepare threshold value. */
    private static final String PREPARE_THRESHOLD = "0";
    
    /**
     * Constructs a new DatabaseConnection with the specified properties.
     * 
     * @param connectionProperties the connection properties
     */
    @Inject
    public DatabaseConnection(final Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
        if (instance == null) {
            instance = this;
        }
    }
    
    /**
     * Gets the singleton instance.
     * 
     * @return the database connection instance
     * @throws IllegalStateException if not initialized
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseConnection не инициализирован");
        }
        return instance;
    }
    
    /**
     * Gets a database connection.
     * 
     * @return the database connection
     * @throws ConnectionException if connection fails
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            return connection;
        } catch (SQLException e) {
            throw new ConnectionException("Ошибка при получении соединения", e);
        }
    }
    
    /**
     * Establishes a database connection.
     * 
     * @throws ConnectionException if connection fails
     */
    private synchronized void connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            
            String url = connectionProperties.getProperty("db.url");
            String user = connectionProperties.getProperty("db.username");
            String password = connectionProperties.getProperty("db.password");
            
            if (url == null || user == null || password == null) {
                throw new ConnectionException("Не указаны параметры подключения к БД");
            }
            
            Class.forName(POSTGRESQL_DRIVER);
            
            Properties props = new Properties();
            props.setProperty("user", user);
            props.setProperty("password", password);
            props.setProperty("ssl", connectionProperties.getProperty("db.ssl", "false"));
            props.setProperty("prepareThreshold", PREPARE_THRESHOLD);
            
            connection = DriverManager.getConnection(url, props);
            
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            
        } catch (ClassNotFoundException e) {
            throw new ConnectionException("Драйвер PostgreSQL не найден", e);
        } catch (SQLException e) {
            throw new ConnectionException("Ошибка подключения к БД", e);
        }
    }
    
    /**
     * Closes the database connection.
     * 
     * @throws ConnectionException if closing fails
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            throw new ConnectionException("Ошибка при закрытии соединения", e);
        }
    }
    
    /**
     * Checks if the connection is established.
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Begins a transaction.
     * 
     * @throws ConnectionException if operation fails
     */
    public void beginTransaction() {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new ConnectionException("Ошибка при начале транзакции", e);
        }
    }
    
    /**
     * Commits the current transaction.
     * 
     * @throws ConnectionException if commit fails
     */
    public void commitTransaction() {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (SQLException e) {
            throw new ConnectionException("Ошибка при коммите транзакции", e);
        }
    }
    
    /**
     * Rolls back the current transaction.
     * 
     * @throws ConnectionException if rollback fails
     */
    public void rollbackTransaction() {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new ConnectionException("Ошибка при откате транзакции", e);
        }
    }
    
    /**
     * Sets auto-commit mode.
     * 
     * @param autoCommit the auto-commit value
     * @throws ConnectionException if operation fails
     */
    public void setAutoCommit(final boolean autoCommit) {
        try {
            if (connection != null) {
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            throw new ConnectionException("Ошибка при настройке autoCommit", e);
        }
    }
}
package jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages database transactions.
 * Provides methods to execute operations within transactions.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class TransactionManager {
    
    /** The database connection. */
    private DatabaseConnection databaseConnection;
    
    @Autowired
    public TransactionManager() {
    }
    /**
     * Constructs a new TransactionManager with the specified connection.
     * 
     * @param databaseConnection the database connection
     */
    @Autowired
    public TransactionManager(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
    
    /**
     * Executes an operation within a transaction.
     * 
     * @param operation the operation to execute
     */
    public void executeInTransaction(final Runnable operation) {
        executeInTransaction(() -> {
            operation.run();
            return null;
        });
    }
    
    /**
     * Executes an operation within a transaction and returns a result.
     * 
     * @param <T> the result type
     * @param operation the operation to execute
     * @return the result of the operation
     */
    public <T> T executeInTransaction(final TransactionOperation<T> operation) {
        Connection conn = null;
        boolean originalAutoCommit = false;
        
        try {
            conn = databaseConnection.getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            
            T result = operation.execute();
            
            conn.commit();
            return result;
            
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw new TransactionException("Ошибка выполнения транзакции", e);
        } finally {
            restoreAutoCommit(conn, originalAutoCommit);
        }
    }
    
    /**
     * Rolls back a transaction.
     * 
     * @param conn the database connection
     */
    private void rollbackTransaction(final Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                throw new TransactionException("Ошибка при откате транзакции", rollbackEx);
            }
        }
    }
    
    /**
     * Restores auto-commit mode.
     * 
     * @param conn the database connection
     * @param originalAutoCommit the original auto-commit value
     */
    private void restoreAutoCommit(final Connection conn, final boolean originalAutoCommit) {
        if (conn != null) {
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                throw new TransactionException("Ошибка восстановления autoCommit", e);
            }
        }
    }
    
    /**
     * Executes an operation with result.
     * 
     * @param <T> the result type
     * @param function the function to execute
     * @return the result
     */
    public <T> T executeWithResult(final TransactionFunction<T> function) {
        return executeInTransaction(function::apply);
    }
    
    /**
     * Executes an operation without result.
     * 
     * @param consumer the consumer to execute
     */
    public void executeWithoutResult(final TransactionConsumer consumer) {
        executeInTransaction(() -> {
            consumer.accept();
            return null;
        });
    }
    
    /**
     * Functional interface for transaction operations.
     * 
     * @param <T> the result type
     */
    @FunctionalInterface
    public interface TransactionOperation<T> {
        
        /**
         * Executes the operation.
         * 
         * @return the result
         * @throws Exception if execution fails
         */
        T execute() throws Exception;
    }
    
    /**
     * Functional interface for transaction functions.
     * 
     * @param <T> the result type
     */
    @FunctionalInterface
    public interface TransactionFunction<T> {
        
        /**
         * Applies the function.
         * 
         * @return the result
         * @throws Exception if execution fails
         */
        T apply() throws Exception;
    }
    
    /**
     * Functional interface for transaction consumers.
     */
    @FunctionalInterface
    public interface TransactionConsumer {
        
        /**
         * Accepts the consumer.
         * 
         * @throws Exception if execution fails
         */
        void accept() throws Exception;
    }
}
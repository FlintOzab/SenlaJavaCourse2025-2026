package bookstore.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import bookstore.jdbc.TransactionException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Manages JPA transactions.
 * Provides methods to execute operations within transactions.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class JpaTransactionManager {
    
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaTransactionManager.class);
    
    /** Entity manager for JPA operations. */
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Executes an operation within a transaction and returns a result.
     * 
     * @param <T> the result type
     * @param operation the operation to execute
     * @return the result of the operation
     * @throws TransactionException if transaction fails
     */
    @Transactional
    public <T> T executeInTransaction(final TransactionOperation<T> operation) {
        try {
            LOGGER.debug("Executing operation in transaction");
            T result = operation.execute(entityManager);
            LOGGER.debug("Transaction completed successfully");
            return result;
        } catch (Exception e) {
            LOGGER.error("Transaction failed", e);
            throw new TransactionException("Transaction execution failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Executes an operation without a result.
     * 
     * @param consumer the operation to execute
     * @throws TransactionException if transaction fails
     */
    @Transactional
    public void executeWithoutResult(final TransactionConsumer consumer) {
        executeInTransaction(em -> {
            consumer.accept(em);
            return null;
        });
    }
    
    /**
     * Executes a query operation within a transaction.
     * 
     * @param <T> the result type
     * @param operation the query operation
     * @return the query result
     * @throws TransactionException if transaction fails
     */
    @Transactional(readOnly = true)
    public <T> T executeQuery(final TransactionQuery<T> operation) {
        return executeInTransaction(operation::execute);
    }
    
    /**
     * Functional interface for transaction operations with result.
     * 
     * @param <T> the result type
     */
    @FunctionalInterface
    public interface TransactionOperation<T> {
        
        /**
         * Executes the operation with the given EntityManager.
         * 
         * @param em the EntityManager
         * @return the result
         * @throws Exception if execution fails
         */
        T execute(EntityManager em) throws Exception;
    }
    
    /**
     * Functional interface for transaction consumers.
     */
    @FunctionalInterface
    public interface TransactionConsumer {
        
        /**
         * Accepts the EntityManager.
         * 
         * @param em the EntityManager
         * @throws Exception if execution fails
         */
        void accept(EntityManager em) throws Exception;
    }
    
    /**
     * Functional interface for query operations.
     * 
     * @param <T> the result type
     */
    @FunctionalInterface
    public interface TransactionQuery<T> {
        
        /**
         * Executes the query with the given EntityManager.
         * 
         * @param em the EntityManager
         * @return the query result
         * @throws Exception if execution fails
         */
        T execute(EntityManager em) throws Exception;
    }
}
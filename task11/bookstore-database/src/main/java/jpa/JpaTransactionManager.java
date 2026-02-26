package jpa;

import jdbc.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

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
    
    /** Thread-local EntityManager for transaction context. */
    private final ThreadLocal<EntityManager> entityManagerHolder = new ThreadLocal<>();
    
    /** Thread-local transaction flag. */
    private final ThreadLocal<Boolean> transactionActive = new ThreadLocal<>();
    
    /** EntityManager factory. */
    private final JpaEntityManagerFactory entityManagerFactory;
    
    /**
     * Constructs a new JpaTransactionManager with the specified factory.
     * 
     * @param entityManagerFactory the entity manager factory
     */
    @Autowired
    public JpaTransactionManager(final JpaEntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
    
    /**
     * Gets the current EntityManager for the thread.
     * Creates a new one if none exists.
     * 
     * @return the EntityManager
     */
    public EntityManager getCurrentEntityManager() {
        EntityManager em = entityManagerHolder.get();
        if (em == null || !em.isOpen()) {
            em = entityManagerFactory.createEntityManager();
            entityManagerHolder.set(em);
            LOGGER.debug("Created new EntityManager for thread: {}", Thread.currentThread().getName());
        }
        return em;
    }
    
    /**
     * Executes an operation within a transaction and returns a result.
     * 
     * @param <T> the result type
     * @param operation the operation to execute
     * @return the result of the operation
     * @throws TransactionException if transaction fails
     */
    public <T> T executeInTransaction(final TransactionOperation<T> operation) {
        EntityManager em = getCurrentEntityManager();
        EntityTransaction transaction = em.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        
        try {
            if (isNewTransaction) {
                transaction.begin();
                transactionActive.set(true);
                LOGGER.debug("Started new transaction");
            }
            
            T result = operation.execute(em);
            
            if (isNewTransaction) {
                transaction.commit();
                transactionActive.remove();
                LOGGER.debug("Transaction committed successfully");
            }
            
            return result;
            
        } catch (Exception e) {
            if (isNewTransaction && transaction.isActive()) {
                try {
                    transaction.rollback();
                    LOGGER.error("Transaction rolled back due to error", e);
                } catch (Exception rollbackEx) {
                    LOGGER.error("Error during transaction rollback", rollbackEx);
                    throw new TransactionException("Failed to rollback transaction", rollbackEx);
                }
            }
            throw new TransactionException("Transaction execution failed: " + e.getMessage(), e);
        } finally {
            if (isNewTransaction) {
                closeEntityManager();
            }
        }
    }
    
    /**
     * Executes an operation without a result.
     * 
     * @param consumer the operation to execute
     * @throws TransactionException if transaction fails
     */
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
    public <T> T executeQuery(final TransactionQuery<T> operation) {
        return executeInTransaction(operation::execute);
    }
    
    /**
     * Closes the EntityManager for the current thread.
     */
    public void closeEntityManager() {
        EntityManager em = entityManagerHolder.get();
        if (em != null && em.isOpen()) {
            em.close();
            entityManagerHolder.remove();
            LOGGER.debug("Closed EntityManager for thread: {}", Thread.currentThread().getName());
        }
    }
    
    /**
     * Clears the persistence context.
     */
    public void clear() {
        EntityManager em = entityManagerHolder.get();
        if (em != null && em.isOpen()) {
            em.clear();
        }
    }
    
    /**
     * Flushes the persistence context.
     */
    public void flush() {
        EntityManager em = entityManagerHolder.get();
        if (em != null && em.isOpen()) {
            em.flush();
        }
    }
    
    /**
     * Checks if a transaction is active for the current thread.
     * 
     * @return true if transaction is active
     */
    public boolean isTransactionActive() {
        Boolean active = transactionActive.get();
        return active != null && active;
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
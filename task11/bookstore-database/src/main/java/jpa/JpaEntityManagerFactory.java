package jpa;

import jdbc.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Factory for creating JPA EntityManager instances.
 * Manages the EntityManagerFactory lifecycle.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class JpaEntityManagerFactory {
    
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaEntityManagerFactory.class);
    
    /** EntityManagerFactory instance. */
    private EntityManagerFactory entityManagerFactory;
    
    /** Database connection properties. */
    private final Properties connectionProperties;
    
    /** Flag indicating if factory is initialized. */
    private boolean initialized = false;
    
    /**
     * Constructs a new JpaEntityManagerFactory with the specified properties.
     * 
     * @param connectionProperties the database connection properties
     */
    @Autowired
    public JpaEntityManagerFactory(final Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
        initializeFactory();
    }
    
    /**
     * Initializes the EntityManagerFactory with properties from connectionProperties.
     */
    private synchronized void initializeFactory() {
        if (!initialized) {
            LOGGER.info("Initializing JPA EntityManagerFactory");
            try {
                Map<String, Object> properties = new HashMap<>();
                
                // Set Hibernate properties from connection properties
                properties.put("jakarta.persistence.jdbc.url", 
                    connectionProperties.getProperty("db.url"));
                properties.put("jakarta.persistence.jdbc.user", 
                    connectionProperties.getProperty("db.username"));
                properties.put("jakarta.persistence.jdbc.password", 
                    connectionProperties.getProperty("db.password"));
                
                // Additional Hibernate properties
                properties.put("hibernate.connection.driver_class", 
                    "org.postgresql.Driver");
                properties.put("hibernate.dialect", 
                    "org.hibernate.dialect.PostgreSQLDialect");
                properties.put("hibernate.show_sql", 
                    connectionProperties.getProperty("hibernate.show_sql", "false"));
                properties.put("hibernate.format_sql", 
                    connectionProperties.getProperty("hibernate.format_sql", "false"));
                properties.put("hibernate.hbm2ddl.auto", 
                    connectionProperties.getProperty("hibernate.hbm2ddl.auto", "update"));
                
                // Connection pool settings
                properties.put("hibernate.c3p0.min_size", 
                    connectionProperties.getProperty("hibernate.c3p0.min_size", "5"));
                properties.put("hibernate.c3p0.max_size", 
                    connectionProperties.getProperty("hibernate.c3p0.max_size", "20"));
                
                entityManagerFactory = Persistence.createEntityManagerFactory(
                    JpaConstants.PERSISTENCE_UNIT_NAME, properties);
                
                initialized = true;
                LOGGER.info("JPA EntityManagerFactory initialized successfully");
                
            } catch (Exception e) {
                LOGGER.error("Failed to initialize JPA EntityManagerFactory", e);
                throw new ConnectionException("Failed to initialize JPA", e);
            }
        }
    }
    
    /**
     * Creates a new EntityManager.
     * 
     * @return a new EntityManager instance
     * @throws ConnectionException if creation fails
     */
    public EntityManager createEntityManager() {
        try {
            if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
                initializeFactory();
            }
            return entityManagerFactory.createEntityManager();
        } catch (Exception e) {
            LOGGER.error("Failed to create EntityManager", e);
            throw new ConnectionException("Failed to create EntityManager", e);
        }
    }
    
    /**
     * Closes the EntityManagerFactory.
     */
    public synchronized void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            initialized = false;
            LOGGER.info("JPA EntityManagerFactory closed");
        }
    }
    
    /**
     * Checks if the factory is initialized.
     * 
     * @return true if initialized
     */
    public boolean isInitialized() {
        return initialized && entityManagerFactory != null && entityManagerFactory.isOpen();
    }
    
    /**
     * Gets the underlying EntityManagerFactory.
     * 
     * @return the EntityManagerFactory
     */
    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
}
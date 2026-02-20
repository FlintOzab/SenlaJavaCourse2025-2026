package app;

import config.BookstoreConfig;
import config.ConfigModule;
import app.control.BookstoreController;
import app.control.ConsoleMenuController;
import di.DependencyInjector;
import service.Bookstore;
import service.StateManager;
import view.ConsoleDisplay;
import view.ConsoleInput;
import dao.DAOFactory;
import jdbc.DatabaseConnection;
import jdbc.TransactionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Set;

/**
 * Main entry point for the Bookstore application.
 * Initializes the dependency injection container and starts the application.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public final class Main {
    
    /** Logger instance for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    /** Database URL property key. */
    private static final String DB_URL_PROPERTY = "db.url";
    
    /** Database username property key. */
    private static final String DB_USERNAME_PROPERTY = "db.username";
    
    /** Database password property key. */
    private static final String DB_PASSWORD_PROPERTY = "db.password";
    
    /** Database SSL property key. */
    private static final String DB_SSL_PROPERTY = "db.ssl";
    
    /** Default database URL. */
    private static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/bookstore_db";
    
    /** Default database username. */
    private static final String DEFAULT_DB_USERNAME = "test";
    
    /** Default database password. */
    private static final String DEFAULT_DB_PASSWORD = "test";
    
    /** Default SSL setting. */
    private static final String DEFAULT_DB_SSL = "false";
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Main() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * Main method to start the application.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(final String[] args) {
        try {
            LOGGER.info("Starting Bookstore Application");
            DependencyInjector container = createContainer();
            
            autoConfigureComponents(container);
            
            ShopApp app = container.getComponent(ShopApp.class);
            app.run();
            
            LOGGER.info("Bookstore Application finished successfully");
        } catch (Exception e) {
            LOGGER.error("Critical error in main application", e);
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates and configures the dependency injection container.
     * 
     * @return the configured dependency injector
     */
    private static DependencyInjector createContainer() {
        LOGGER.debug("Creating dependency injection container");
        DependencyInjector.Builder builder = DependencyInjector.builder();
        
        Properties dbProperties = createDatabaseProperties();
        builder.withComponentInstance(Properties.class, dbProperties);
        
        return builder.withComponent(DatabaseConnection.class)
               .withComponent(TransactionManager.class)
               .withComponent(DAOFactory.class)
               .withComponent(BookstoreConfig.class)
               .withComponent(Bookstore.class)
               .withComponent(StateManager.class)
               .withComponent(BookstoreController.class)
               .withComponent(ConsoleDisplay.class)
               .withComponent(ConsoleInput.class)
               .withComponent(ConsoleMenuController.class)
               .withComponent(ShopApp.class)
               .build();
    }
    
    /**
     * Creates database connection properties.
     * 
     * @return properties object with database configuration
     */
    private static Properties createDatabaseProperties() {
        Properties dbProperties = new Properties();
        dbProperties.setProperty(DB_URL_PROPERTY, DEFAULT_DB_URL);
        dbProperties.setProperty(DB_USERNAME_PROPERTY, DEFAULT_DB_USERNAME);
        dbProperties.setProperty(DB_PASSWORD_PROPERTY, DEFAULT_DB_PASSWORD);
        dbProperties.setProperty(DB_SSL_PROPERTY, DEFAULT_DB_SSL);
        LOGGER.debug("Database properties configured: {}", DEFAULT_DB_URL);
        return dbProperties;
    }
    
    /**
     * Auto-configures all components in the container.
     * 
     * @param container the dependency injector containing all components
     */
    private static void autoConfigureComponents(final DependencyInjector container) {
        LOGGER.debug("Starting auto-configuration of components");
        try {
            Set<Object> allInstances = container.getAllInstances();
            
            for (Object instance : allInstances) {
                configureSingleInstance(instance);
            }
            
        } catch (Exception e) {
            LOGGER.error("Error during auto-configuration", e);
            System.err.println("Ошибка автоматической конфигурации: " + e.getMessage());
        }
    }
    
    /**
     * Configures a single component instance.
     * 
     * @param instance the component instance to configure
     */
    private static void configureSingleInstance(final Object instance) {
        try {
            if (ConfigModule.hasConfigProperties(instance)) {
                LOGGER.info("Configuring component: {}", instance.getClass().getName());
                ConfigModule.configure(instance);
            }
        } catch (Exception e) {
            LOGGER.error("Error configuring component: {}", instance.getClass().getName(), e);
            System.err.println("Ошибка конфигурации " 
                + instance.getClass().getName() + ": " + e.getMessage());
        }
    }
}
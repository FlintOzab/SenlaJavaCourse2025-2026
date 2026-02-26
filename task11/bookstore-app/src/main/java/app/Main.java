package app;

import config.AppConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Main entry point for the Bookstore application.
 * Initializes Spring context and starts the application.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public final class Main {
    
    /** Logger instance for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
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
        ApplicationContext context = null;
        
        try {
            LOGGER.info("Starting Bookstore Application with Spring Framework");
            
            // Create Spring application context
            context = new AnnotationConfigApplicationContext(AppConfig.class);
            
            // Get and run the main application bean
            ShopApp app = context.getBean(ShopApp.class);
            app.run();
            
            LOGGER.info("Bookstore Application finished successfully");
            
        } catch (Exception e) {
            LOGGER.error("Critical error in main application", e);
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close Spring context if it was created
            if (context != null && context instanceof AnnotationConfigApplicationContext) {
                ((AnnotationConfigApplicationContext) context).close();
            }
        }
    }
}
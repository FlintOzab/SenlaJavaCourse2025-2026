package bookstore.exception;

/**
 * Exception thrown when configuration errors occur.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class ConfigurationException extends RuntimeException {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new configuration exception with the specified message.
     * 
     * @param message the detail message
     */
    public ConfigurationException(final String message) {
        super(message);
    }
    
    /**
     * Constructs a new configuration exception with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
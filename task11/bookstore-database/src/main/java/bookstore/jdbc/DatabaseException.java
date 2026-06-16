package bookstore.jdbc;

/**
 * Base exception for database-related errors.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class DatabaseException extends RuntimeException {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new database exception with the specified message.
     * 
     * @param message the detail message
     */
    public DatabaseException(final String message) {
        super(message);
    }
    
    /**
     * Constructs a new database exception with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public DatabaseException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new database exception with the specified cause.
     * 
     * @param cause the cause
     */
    public DatabaseException(final Throwable cause) {
        super(cause);
    }
}
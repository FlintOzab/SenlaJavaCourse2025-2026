package jdbc;

/**
 * Exception thrown when database connection errors occur.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class ConnectionException extends DatabaseException {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new connection exception with the specified message.
     * 
     * @param message the detail message
     */
    public ConnectionException(final String message) {
        super(message);
    }
    
    /**
     * Constructs a new connection exception with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public ConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
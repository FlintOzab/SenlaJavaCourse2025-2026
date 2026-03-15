package bookstore.jdbc;

/**
 * Exception thrown when transaction operations fail.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class TransactionException extends DatabaseException {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new transaction exception with the specified message.
     * 
     * @param message the detail message
     */
    public TransactionException(final String message) {
        super(message);
    }
    
    /**
     * Constructs a new transaction exception with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public TransactionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
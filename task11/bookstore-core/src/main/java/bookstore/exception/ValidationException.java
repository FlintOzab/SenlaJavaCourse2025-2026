package bookstore.exception;

/**
 * Exception thrown when validation errors occur.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class ValidationException extends BookstoreException {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new validation exception with the specified message.
     * 
     * @param message the detail message
     */
    public ValidationException(final String message) {
        super(message);
    }
    
    /**
     * Constructs a new validation exception with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public ValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
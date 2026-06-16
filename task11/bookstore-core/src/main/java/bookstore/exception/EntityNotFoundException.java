package bookstore.exception;

/**
 * Exception thrown when an entity is not found.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class EntityNotFoundException extends BookstoreException {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new entity not found exception with the specified message.
     * 
     * @param message the detail message
     */
    public EntityNotFoundException(final String message) {
        super(message);
    }
    
    /**
     * Constructs a new entity not found exception with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public EntityNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
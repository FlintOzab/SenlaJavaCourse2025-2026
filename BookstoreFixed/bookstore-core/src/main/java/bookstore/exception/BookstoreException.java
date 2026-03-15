package bookstore.exception;

/**
 * Base exception class for bookstore application.
 * All custom exceptions should extend this class.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class BookstoreException extends Exception {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new bookstore exception with the specified message.
     * 
     * @param message the detail message
     */
    public BookstoreException(final String message) {
        super(message);
    }
    
    /**
     * Constructs a new bookstore exception with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public BookstoreException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
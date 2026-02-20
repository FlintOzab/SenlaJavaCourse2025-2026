package exception;

/**
 * Exception thrown when CSV import operations fail.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class CSVImportException extends BookstoreException {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new CSV import exception with the specified message.
     * 
     * @param message the detail message
     */
    public CSVImportException(final String message) {
        super(message);
    }
    
    /**
     * Constructs a new CSV import exception with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public CSVImportException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
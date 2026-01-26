package t4.exception;

public class ValidationException extends BookstoreException {
	public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

package t4.exception;

public class EntityNotFoundException extends BookstoreException {
	public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

package t4;

public class BookstoreException extends Exception {
	
	public BookstoreException(String message) {
        super(message);
    }
    
    public BookstoreException(String message, Throwable cause) {
        super(message, cause);
    }
}

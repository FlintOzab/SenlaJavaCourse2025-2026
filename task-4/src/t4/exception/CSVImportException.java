package t4.exception;

public class CSVImportException extends BookstoreException {
	
	public CSVImportException(String message) {
        super(message);
    }
    
    public CSVImportException(String message, Throwable cause) {
        super(message, cause);
    }
}

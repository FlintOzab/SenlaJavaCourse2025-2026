package t4;

public class Request {
	    private static int requestCounter = 0;
	    private int requestId;
	    private Order order;
	    private Book book;
	    private boolean done;
	    
	    public Request(Order order, Book book) {
	        this.requestId = requestCounter++;
	        this.order = order;
	        this.book = book;
	        this.done = false;
	        
	    }
	    
	    public int getRequestId() { return requestId; }
	    public Order getCustomerId() { return this.order; }
	    public Book getBook() { return this.book; }
	    public boolean isDone() { return done; }
	    
	    public void setDone(boolean done) { this.done = done; }
	    
	    public void closeRequest()
	    {
	    	setDone(Boolean.TRUE);
	    }
	    
	    public boolean matchesBook(Book book) {
	        return this.book.getIsbn().equals(book.getIsbn());
	    }
}
	

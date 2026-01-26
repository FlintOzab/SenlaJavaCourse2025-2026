package t4.model;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
	private static int requestCounter = 0;
    private int id;
    private int requestId;
    private Order order;
    private Book book;
    private boolean done;
    
    public Request(Order order, Book book) {
        this.id = requestCounter++;
        this.requestId = this.id;
        this.order = order;
        this.book = book;
        this.done = false;
    }
    
    public Request(int id, int requestId, Order order, Book book, boolean done) {
        this.id = id;
        this.requestId = requestId;
        this.order = order;
        this.book = book;
        this.done = done;
        requestCounter = Math.max(requestCounter, id + 1);
    }
    
    public int getId() { return id; }
    public int getRequestId() { return requestId; }
    public Order getOrder() { return this.order; }
    public Book getBook() { return this.book; }
    public boolean isDone() { return done; }
    
    public void setId(int id) { this.id = id; }
    public void setDone(boolean done) { this.done = done; }
    public void setOrder(Order order) { this.order = order; }
    public void setBook(Book book) { this.book = book; }
    
    public void closeRequest() {
        setDone(true);
    }
    
    public boolean matchesBook(Book book) {
        return this.book.getIsbn().equals(book.getIsbn());
    }
    
    @Override
    public String toString() {
        return String.format("ID: %d, Request #%d - Book: %s - Done: %s", 
                           id, requestId, book.getTitle(), done);
    }
    
    public String toCSV() {
        return String.format("%d,%d,%d,%d,%s",
                id, requestId, order.getOrderId(), book.getId(), done);
    }
}
	

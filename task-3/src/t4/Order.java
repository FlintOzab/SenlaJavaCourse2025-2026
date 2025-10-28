package t4;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import t4.Book.BookStatus;

public class Order {
	enum OrderStatus {
	    NEW,
	    COMPLETED,
	    CANCELLED
	}
	private static int orderCounter = 0;
    private int orderId;
    private List<Book> books;
    private OrderStatus status;
    private Date date;
    
    public Order(int Id, List<Book> books) {
        this.orderId = Id;
        this.books = new ArrayList<>(books);
        this.status = OrderStatus.NEW;
        this.date = new Date();
        orderCounter++;
    }
    public void setDate(Date date) { this.date = date; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public int getOrderId() { return orderId; }
    public List<Book> getBooks() { return this.books; }
    public OrderStatus getStatus() { return this.status; }
    public Date getCreationDate() { return this.date; }
    
    public boolean containsOutOfStockBooks() {
        return books.stream().anyMatch(book -> book.getStatus() == BookStatus.OUT_OF_STOCK);
    }
    
    public List<Book> getOutOfStockBooks() {
    	return books.stream().filter(book -> book.getStatus() == Book.BookStatus.OUT_OF_STOCK).collect(Collectors.toList());
    }
    
    public void completeOrder()
    {
    	if (!containsOutOfStockBooks())
    	{
    		this.setStatus(OrderStatus.COMPLETED);
    	}
    }
    
    public void cancelOrder()
    {
    		this.setStatus(OrderStatus.CANCELLED);
    		orderCounter--;
    }
    
    public void getRequestsOnBooksOutOfStock(List<Request> existingRequests)
    {
    	for (Book book: getOutOfStockBooks())
    	{
    		if (!existingRequests.stream().anyMatch(stock -> stock.matchesBook(book)) )
    		{
    			existingRequests.add(new Request(this, book));
    		}
    	}
    }
    

}

package t4;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import t4.Book.BookStatus;

class Order {
    enum OrderStatus {
        NEW,
        COMPLETED,
        CANCELLED
    }
    
    private static int orderCounter = 0;
    private int orderId;
    private List<Book> books;
    private OrderStatus status;
    private Date creationDate;
    private Date completionDate;
    
    public Order(int Id, List<Book> books) {
        this.orderId = Id;
        this.books = new ArrayList<>(books);
        this.status = OrderStatus.NEW;
        this.creationDate = new Date();
        orderCounter++;
    }
    
    public void setCompletionDate(Date date) { this.completionDate = date; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public int getOrderId() { return orderId; }
    public List<Book> getBooks() { return this.books; }
    public OrderStatus getStatus() { return this.status; }
    public Date getCreationDate() { return this.creationDate; }
    public Date getCompletionDate() { return this.completionDate; }
    
    public long getTotalPrice() {
        return books.stream().mapToLong(Book::getPrice).sum();
    }
    
    public boolean containsOutOfStockBooks() {
        return books.stream().anyMatch(book -> book.getStatus() == Book.BookStatus.OUT_OF_STOCK);
    }
    
    public List<Book> getOutOfStockBooks() {
        return books.stream()
            .filter(book -> book.getStatus() == Book.BookStatus.OUT_OF_STOCK)
            .collect(Collectors.toList());
    }
    
    public void completeOrder() {
        if (!containsOutOfStockBooks()) {
            this.setStatus(OrderStatus.COMPLETED);
            this.completionDate = new Date();
        }
    }
    
    public void cancelOrder() {
        this.setStatus(OrderStatus.CANCELLED);
    }
    
    public void getRequestsOnBooksOutOfStock(List<Request> existingRequests) {
        for (Book book : getOutOfStockBooks()) {
            if (!existingRequests.stream().anyMatch(stock -> stock.matchesBook(book))) {
                existingRequests.add(new Request(this, book));
            }
        }
    }
}

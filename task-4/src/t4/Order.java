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
    private int id;
    private int orderId;
    private List<Book> books;
    private OrderStatus status;
    private Date creationDate;
    private Date completionDate;
    
    public Order(int orderId, List<Book> books) {
        this.id = orderCounter++;
        this.orderId = orderId;
        this.books = new ArrayList<>(books);
        this.status = OrderStatus.NEW;
        this.creationDate = new Date();
    }
    
    public Order(int id, int orderId, List<Book> books, OrderStatus status, 
                 Date creationDate, Date completionDate) {
        this.id = id;
        this.orderId = orderId;
        this.books = new ArrayList<>(books);
        this.status = status;
        this.creationDate = creationDate;
        this.completionDate = completionDate;
        orderCounter = Math.max(orderCounter, id + 1);
    }
    
    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public List<Book> getBooks() { return this.books; }
    public OrderStatus getStatus() { return this.status; }
    public Date getCreationDate() { return this.creationDate; }
    public Date getCompletionDate() { return this.completionDate; }
    
    public void setId(int id) { this.id = id; }
    public void setCompletionDate(Date date) { this.completionDate = date; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setBooks(List<Book> books) { this.books = books; }
    
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
    
    @Override
    public String toString() {
        return String.format("ID: %d, Order #%d - Status: %s - Total: %d руб.", 
                           id, orderId, status, getTotalPrice());
    }
    
    public String toCSV() {
        String bookIds = books.stream()
                .map(book -> String.valueOf(book.getId()))
                .collect(Collectors.joining(";"));
        return String.format("%d,%d,%s,%s,%s,%s",
                id, orderId, bookIds, status,
                creationDate.getTime(),
                completionDate != null ? String.valueOf(completionDate.getTime()) : "");
    }
}

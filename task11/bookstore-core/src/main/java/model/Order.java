package model;

import exception.CSVImportException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a customer order in the bookstore.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class Order implements Serializable {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Default ID for new orders. */
    private static final int DEFAULT_ID = 0;
    
    /** Default timestamp for missing dates. */
    private static final long DEFAULT_TIMESTAMP = 0L;
    
    /**
     * Enumeration of possible order statuses.
     */
    public enum OrderStatus {
        /** New order, not yet processed. */
        NEW,
        
        /** Completed order. */
        COMPLETED,
        
        /** Cancelled order. */
        CANCELLED
    }
       
    /** Order ID. */
    private Integer id;
    
    /** Books in the order. */
    private List<Book> books;
    
    /** Order status. */
    private OrderStatus status;
    
    /** Order creation date. */
    private Date creationDate;
    
    /** Order completion date. */
    private Date completionDate;
    
    /** Creation timestamp. */
    private Date createdAt;
    
    /** Last update timestamp. */
    private Date updatedAt;
    
    /**
     * Default constructor.
     */
    public Order() {
        this.books = new ArrayList<>();
    }
    
    /**
     * Constructs a new order without ID.
     * 
     * @param books the books in the order
     */
    public Order(final List<Book> books) {
        this.books = new ArrayList<>(books);
        this.status = OrderStatus.NEW;
        this.creationDate = new Date();
        this.completionDate = null;
    }
    
    /**
     * Constructs a new order with all fields.
     * 
     * @param id the order ID
     * @param books the books in the order
     * @param status the order status
     * @param creationDate the creation date
     * @param completionDate the completion date
     */
    public Order(final Integer id, final List<Book> books,
                  final OrderStatus status, final Date creationDate,
                  final Date completionDate) {
        this.id = id;
        this.books = new ArrayList<>(books);
        this.status = status;
        this.creationDate = creationDate;
        this.completionDate = completionDate;
    }

    /**
     * Gets the order ID.
     * 
     * @return the ID
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Sets the order ID.
     * 
     * @param id the new ID
     */
    public void setId(final Integer id) {
        this.id = id;
    }
    
    /**
     * Gets the books in the order.
     * 
     * @return copy of the books list
     */
    public List<Book> getBooks() {
        return new ArrayList<>(books);
    }
    
    /**
     * Sets the books in the order.
     * 
     * @param books the new books list
     */
    public void setBooks(final List<Book> books) {
        this.books = new ArrayList<>(books);
    }
    
    /**
     * Gets the order status.
     * 
     * @return the status
     */
    public OrderStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the order status.
     * 
     * @param status the new status
     */
    public void setStatus(final OrderStatus status) {
        this.status = status;
    }
    
    /**
     * Gets the creation date.
     * 
     * @return the creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }
    
    /**
     * Sets the creation date.
     * 
     * @param creationDate the new creation date
     */
    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }
    
    /**
     * Gets the completion date.
     * 
     * @return the completion date
     */
    public Date getCompletionDate() {
        return completionDate;
    }
    
    /**
     * Sets the completion date.
     * 
     * @param completionDate the new completion date
     */
    public void setCompletionDate(final Date completionDate) {
        this.completionDate = completionDate;
    }
    
    /**
     * Gets the creation timestamp.
     * 
     * @return the creation timestamp
     */
    public Date getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp.
     * 
     * @param createdAt the new creation timestamp
     */
    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the last update timestamp.
     * 
     * @return the last update timestamp
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets the last update timestamp.
     * 
     * @param updatedAt the new last update timestamp
     */
    public void setUpdatedAt(final Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Calculates total price of the order.
     * 
     * @return the total price
     */
    public long getTotalPrice() {
        return books.stream()
            .mapToLong(Book::getPrice)
            .sum();
    }
    
    /**
     * Checks if the order contains out of stock books.
     * 
     * @return true if contains out of stock books
     */
    public boolean containsOutOfStockBooks() {
        return books.stream()
            .anyMatch(book -> book.getStatus() == Book.BookStatus.OUT_OF_STOCK);
    }
    
    /**
     * Gets the list of out of stock books in the order.
     * 
     * @return list of out of stock books
     */
    public List<Book> getOutOfStockBooks() {
        return books.stream()
            .filter(book -> book.getStatus() == Book.BookStatus.OUT_OF_STOCK)
            .collect(Collectors.toList());
    }
    
    /**
     * Completes the order if possible.
     */
    public void completeOrder() {
        if (!containsOutOfStockBooks()) {
            this.status = OrderStatus.COMPLETED;
            this.completionDate = new Date();
        }
    }
    
    /**
     * Cancels the order.
     */
    public void cancelOrder() {
        this.status = OrderStatus.CANCELLED;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Заказ #%d - Статус: %s - Сумма: %d руб. - Книг: %d", 
            id, status, getTotalPrice(), books.size());
    }
    
    /**
     * Converts the order to CSV format.
     * 
     * @return CSV string representation
     */
    public String toCSV() {
        String booksList = books.stream()
            .map(Book::toCSV)
            .collect(Collectors.joining(";"));
            
        long creationTime = creationDate != null 
            ? creationDate.getTime() 
            : DEFAULT_TIMESTAMP;
        long completionTime = completionDate != null 
            ? completionDate.getTime() 
            : DEFAULT_TIMESTAMP;
            
        return String.format("%d,%s,%s,%d,%d",
                id != null ? id : DEFAULT_ID,
                booksList,
                status != null ? status : OrderStatus.NEW,
                creationTime,
                completionTime);
    }

    /**
     * Creates an order from CSV line.
     * 
     * @param csvLine the CSV line
     * @return the created order
     * @throws CSVImportException if parsing fails
     */
    public static Order fromCSV(final String csvLine) throws CSVImportException {
        try {
            String[] parts = csvLine.split(",", 5);
            if (parts.length < 5) {
                throw new CSVImportException(
                    "Недостаточно данных для заказа: " + csvLine);
            }
            
            int orderId = Integer.parseInt(parts[0]);
            
            List<Book> bookList = new ArrayList<>();
            String[] bookCSVStrings = parts[1].split(";");
            
            for (String bookCSV : bookCSVStrings) {
                if (!bookCSV.trim().isEmpty()) {
                    try {
                        Book book = Book.fromCSV(bookCSV);
                        bookList.add(book);
                    } catch (CSVImportException e) {
                        throw new CSVImportException(
                            "Ошибка парсинга книги в заказе: " + e.getMessage(), e);
                    }
                }
            }
            
            OrderStatus status = OrderStatus.valueOf(parts[2]);
            Date creationDate = new Date(Long.parseLong(parts[3]));
            Date completionDate = parts[4].isEmpty() 
                ? null 
                : new Date(Long.parseLong(parts[4]));
            
            return new Order(orderId, bookList, status, creationDate, completionDate);
            
        } catch (Exception e) {
            throw new CSVImportException(
                "Ошибка парсинга заказа: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
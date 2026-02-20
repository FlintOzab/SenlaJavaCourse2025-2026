package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Represents a request for a book that is out of stock.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class Request implements Serializable {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Default ID for new requests. */
    private static final int DEFAULT_ID = 0;
    
    /** Request ID. */
    private Integer id;
    
    /** Associated order ID. */
    private Integer orderId;
    
    /** Associated book ID. */
    private Integer bookId;
    
    /** Request completion status. */
    private boolean done;
    
    /** Creation timestamp. */
    private Date createdAt;
    
    /** Last update timestamp. */
    private Date updatedAt;
    
    /** Transient reference to the associated order. */
    private transient Order order;
    
    /** Transient reference to the associated book. */
    private transient Book book;
    
    /**
     * Default constructor.
     */
    public Request() {
    }
    
    /**
     * Constructs a new request without ID.
     * 
     * @param order the associated order
     * @param book the associated book
     */
    public Request(final Order order, final Book book) {
        this.order = order;
        this.book = book;
        this.orderId = order != null ? order.getId() : null;
        this.bookId = book != null ? book.getId() : null;
        this.done = false;
    }
    
    /**
     * Constructs a new request with all fields.
     * 
     * @param id the request ID
     * @param order the associated order
     * @param book the associated book
     * @param done the completion status
     */
    public Request(final Integer id, final Order order,
                    final Book book, final boolean done) {
        this.id = id;
        this.order = order;
        this.book = book;
        this.orderId = order != null ? order.getId() : null;
        this.bookId = book != null ? book.getId() : null;
        this.done = done;
    }

    /**
     * Gets the request ID.
     * 
     * @return the ID
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Sets the request ID.
     * 
     * @param id the new ID
     */
    public void setId(final Integer id) {
        this.id = id;
    }
    
    /**
     * Gets the order ID.
     * 
     * @return the order ID
     */
    public Integer getOrderId() {
        return orderId;
    }
    
    /**
     * Sets the order ID.
     * 
     * @param orderId the new order ID
     */
    public void setOrderId(final Integer orderId) {
        this.orderId = orderId;
    }
    
    /**
     * Gets the book ID.
     * 
     * @return the book ID
     */
    public Integer getBookId() {
        return bookId;
    }
    
    /**
     * Sets the book ID.
     * 
     * @param bookId the new book ID
     */
    public void setBookId(final Integer bookId) {
        this.bookId = bookId;
    }
    
    /**
     * Checks if the request is done.
     * 
     * @return true if done
     */
    public boolean isDone() {
        return done;
    }
    
    /**
     * Sets the completion status.
     * 
     * @param done the new status
     */
    public void setDone(final boolean done) {
        this.done = done;
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
     * Gets the associated order.
     * 
     * @return the order
     */
    public Order getOrder() {
        return order;
    }
    
    /**
     * Sets the associated order and updates order ID.
     * 
     * @param order the new order
     */
    public void setOrder(final Order order) {
        this.order = order;
        this.orderId = order != null ? order.getId() : null;
    }
    
    /**
     * Gets the associated book.
     * 
     * @return the book
     */
    public Book getBook() {
        return book;
    }
    
    /**
     * Sets the associated book and updates book ID.
     * 
     * @param book the new book
     */
    public void setBook(final Book book) {
        this.book = book;
        this.bookId = book != null ? book.getId() : null;
    }
    
    /**
     * Closes the request (marks as done).
     */
    public void closeRequest() {
        setDone(true);
    }
    
    /**
     * Checks if the request matches a book.
     * 
     * @param book the book to check
     * @return true if the request matches the book
     */
    public boolean matchesBook(final Book book) {
        return this.book != null && book != null 
            && this.book.getIsbn().equals(book.getIsbn());
    }
    
    @Override
    public String toString() {
        return String.format(
            "Запрос #%d - Заказ: %d, Книга: %d, Выполнен: %s", 
            id, orderId, bookId, done ? "Да" : "Нет");
    }
    
    /**
     * Converts the request to CSV format.
     * 
     * @return CSV string representation
     */
    public String toCSV() {
        return String.format("%d,%d,%d,%s",
                id != null ? id : DEFAULT_ID,
                orderId != null ? orderId : DEFAULT_ID,
                bookId != null ? bookId : DEFAULT_ID,
                done);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Request request = (Request) o;
        return Objects.equals(id, request.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
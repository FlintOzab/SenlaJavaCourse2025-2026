package bookstore.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.NamedQueries;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Represents a request for a book that is out of stock.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Entity
@Table(name = "requests")
@NamedQueries({
    @NamedQuery(
        name = "Request.findByOrderId",
        query = "SELECT r FROM Request r WHERE r.order.id = :orderId ORDER BY r.createdAt"
    ),
    @NamedQuery(
        name = "Request.findByBookId",
        query = "SELECT r FROM Request r WHERE r.book.id = :bookId ORDER BY r.createdAt"
    ),
    @NamedQuery(
        name = "Request.findActive",
        query = "SELECT r FROM Request r WHERE r.done = false ORDER BY r.createdAt"
    )
})
public class Request implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Default ID for new requests. */
    private static final int DEFAULT_ID = 0;
    
    /** Magic number 4 for array access. */
    private static final int INDEX_4 = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Column(name = "done", nullable = false)
    private boolean done;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
    
    /** Associated order ID (for backward compatibility). */
    private transient Integer orderId;
    
    /** Associated book ID (for backward compatibility). */
    private transient Integer bookId;
    
    /**
     * Default constructor.
     */
    public Request() {
        this.done = false;
    }
    
    /**
     * Constructs a new request without ID.
     * 
     * @param orderValue the associated order
     * @param bookValue the associated book
     */
    public Request(final Order orderValue, final Book bookValue) {
        this.order = orderValue;
        this.book = bookValue;
        if (orderValue != null) {
            this.orderId = orderValue.getId();
        }
        if (bookValue != null) {
            this.bookId = bookValue.getId();
        }
        this.done = false;
    }
    
    /**
     * Constructs a new request with all fields.
     * 
     * @param idValue the request ID
     * @param orderValue the associated order
     * @param bookValue the associated book
     * @param doneValue the completion status
     */
    public Request(final Integer idValue, final Order orderValue,
                    final Book bookValue, final boolean doneValue) {
        this.id = idValue;
        this.order = orderValue;
        this.book = bookValue;
        if (orderValue != null) {
            this.orderId = orderValue.getId();
        }
        if (bookValue != null) {
            this.bookId = bookValue.getId();
        }
        this.done = doneValue;
    }

    /**
     * Lifecycle callback for pre-persist.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }
    
    /**
     * Lifecycle callback for pre-update.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
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
     * @param idValue the new ID
     */
    public void setId(final Integer idValue) {
        this.id = idValue;
    }
    
    /**
     * Gets the order ID.
     * 
     * @return the order ID
     */
    public Integer getOrderId() {
        if (order != null) {
            return order.getId();
        }
        return orderId;
    }
    
    /**
     * Gets the book ID.
     * 
     * @return the book ID
     */
    public Integer getBookId() {
        if (book != null) {
            return book.getId();
        }
        return bookId;
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
     * @param doneValue the new status
     */
    public void setDone(final boolean doneValue) {
        this.done = doneValue;
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
     * @param createdAtValue the new creation timestamp
     */
    public void setCreatedAt(final Date createdAtValue) {
        this.createdAt = createdAtValue;
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
     * @param updatedAtValue the new last update timestamp
     */
    public void setUpdatedAt(final Date updatedAtValue) {
        this.updatedAt = updatedAtValue;
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
     * Sets the associated order.
     * 
     * @param orderValue the new order
     */
    public void setOrder(final Order orderValue) {
        this.order = orderValue;
        if (orderValue != null) {
            this.orderId = orderValue.getId();
        }
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
     * Sets the associated book.
     * 
     * @param bookValue the new book
     */
    public void setBook(final Book bookValue) {
        this.book = bookValue;
        if (bookValue != null) {
            this.bookId = bookValue.getId();
        }
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
     * @param bookValue the book to check
     * @return true if the request matches the book
     */
    public boolean matchesBook(final Book bookValue) {
        return this.book != null && bookValue != null 
            && this.book.getIsbn().equals(bookValue.getIsbn());
    }
    
    @Override
    public String toString() {
        return String.format(
            "Запрос #%d - Заказ: %d, Книга: %d, Выполнен: %s", 
            id, getOrderId(), getBookId(), done ? "Да" : "Нет");
    }
    
    /**
     * Converts the request to CSV format.
     * 
     * @return CSV string representation
     */
    public String toCSV() {
        return String.format("%d,%d,%d,%s",
                id != null ? id : DEFAULT_ID,
                getOrderId() != null ? getOrderId() : DEFAULT_ID,
                getBookId() != null ? getBookId() : DEFAULT_ID,
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
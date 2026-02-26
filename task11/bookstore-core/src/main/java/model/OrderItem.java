package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Represents the many-to-many relationship between Order and Book.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Entity
@Table(name = "order_items")
public class OrderItem implements Serializable {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
    
    /**
     * Default constructor.
     */
    public OrderItem() {
    }
    
    /**
     * Constructs a new order item.
     * 
     * @param orderValue the order
     * @param bookValue the book
     */
    public OrderItem(final Order orderValue, final Book bookValue) {
        this.order = orderValue;
        this.book = bookValue;
    }
    
    /**
     * Lifecycle callback for pre-persist.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
    
    /**
     * Gets the ID.
     * 
     * @return the ID
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Sets the ID.
     * 
     * @param idValue the ID
     */
    public void setId(final Integer idValue) {
        this.id = idValue;
    }
    
    /**
     * Gets the order.
     * 
     * @return the order
     */
    public Order getOrder() {
        return order;
    }
    
    /**
     * Sets the order.
     * 
     * @param orderValue the order
     */
    public void setOrder(final Order orderValue) {
        this.order = orderValue;
    }
    
    /**
     * Gets the book.
     * 
     * @return the book
     */
    public Book getBook() {
        return book;
    }
    
    /**
     * Sets the book.
     * 
     * @param bookValue the book
     */
    public void setBook(final Book bookValue) {
        this.book = bookValue;
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
     * @param createdAtValue the creation timestamp
     */
    public void setCreatedAt(final Date createdAtValue) {
        this.createdAt = createdAtValue;
    }
    
    @Override
    public String toString() {
        return String.format("OrderItem{id=%d, book=%s}",
                id, book != null ? book.getTitle() : "null");
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
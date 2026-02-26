package model;

import exception.CSVImportException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

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
@Entity
@Table(name = "orders")
@NamedQueries({
    @NamedQuery(
        name = "Order.findByStatus",
        query = "SELECT o FROM Order o WHERE o.status = :status ORDER BY o.creationDate DESC"
    ),
    @NamedQuery(
        name = "Order.findCompletedInPeriod",
        query = "SELECT o FROM Order o WHERE o.status = 'COMPLETED' AND o.completionDate BETWEEN :startDate AND :endDate ORDER BY o.completionDate DESC"
    )
})
public class Order implements Serializable {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Default ID for new orders. */
    private static final int DEFAULT_ID = 0;
    
    /** Default timestamp for missing dates. */
    private static final long DEFAULT_TIMESTAMP = 0L;
    
    /** Magic number 5 for array access. */
    private static final int INDEX_5 = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false)
    private Date creationDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "completion_date")
    private Date completionDate;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    @Column(name = "total_price")
    private Long totalPrice;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
    
    /**
     * Books in the order (for backward compatibility).
     */
    private transient List<Book> books;
    
    /**
     * Enumeration of possible order statuses.
     */
    public enum OrderStatus {
        /** New order, not yet processed. */
        NEW,
        
        /** Completed order. */
        COMPLETED,
        
        /** Cancelled order. */
        CANCELLED;
        
        /**
         * Returns a string representation of the enum.
         * 
         * @return the string representation
         */
        @Override
        public String toString() {
            return this.name();
        }
    }

    /**
     * Default constructor.
     */
    public Order() {
        this.status = OrderStatus.NEW;
        this.creationDate = new Date();
        this.books = new ArrayList<>();
    }
    
    /**
     * Constructs a new order without ID.
     * 
     * @param booksValue the books in the order
     */
    public Order(final List<Book> booksValue) {
        this.books = new ArrayList<>(booksValue);
        this.status = OrderStatus.NEW;
        this.creationDate = new Date();
        this.completionDate = null;
        this.orderItems = new ArrayList<>();
        for (Book book : booksValue) {
            addOrderItem(book);
        }
    }
    
    /**
     * Constructs a new order with all fields.
     * 
     * @param idValue the order ID
     * @param booksValue the books in the order
     * @param statusValue the order status
     * @param creationDateValue the creation date
     * @param completionDateValue the completion date
     */
    public Order(final Integer idValue, final List<Book> booksValue,
                  final OrderStatus statusValue, final Date creationDateValue,
                  final Date completionDateValue) {
        this.id = idValue;
        this.books = new ArrayList<>(booksValue);
        this.status = statusValue;
        this.creationDate = creationDateValue;
        this.completionDate = completionDateValue;
        this.orderItems = new ArrayList<>();
        for (Book book : booksValue) {
            addOrderItem(book);
        }
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
     * @param idValue the new ID
     */
    public void setId(final Integer idValue) {
        this.id = idValue;
    }
    
    /**
     * Gets the books in the order.
     * 
     * @return copy of the books list
     */
    public List<Book> getBooks() {
        if (books == null) {
            books = new ArrayList<>();
            if (orderItems != null) {
                for (OrderItem item : orderItems) {
                    if (item.getBook() != null) {
                        books.add(item.getBook());
                    }
                }
            }
        }
        return new ArrayList<>(books);
    }
    
    /**
     * Sets the books in the order.
     * 
     * @param booksValue the new books list
     */
    public void setBooks(final List<Book> booksValue) {
        this.books = new ArrayList<>(booksValue);
        this.orderItems.clear();
        for (Book book : booksValue) {
            addOrderItem(book);
        }
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
     * @param statusValue the new status
     */
    public void setStatus(final OrderStatus statusValue) {
        this.status = statusValue;
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
     * @param creationDateValue the new creation date
     */
    public void setCreationDate(final Date creationDateValue) {
        this.creationDate = creationDateValue;
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
     * @param completionDateValue the new completion date
     */
    public void setCompletionDate(final Date completionDateValue) {
        this.completionDate = completionDateValue;
    }
    
    /**
     * Gets the order items.
     * 
     * @return list of order items
     */
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    
    /**
     * Sets the order items.
     * 
     * @param orderItemsValue the order items
     */
    public void setOrderItems(final List<OrderItem> orderItemsValue) {
        this.orderItems = orderItemsValue;
    }
    
    /**
     * Gets the total price.
     * 
     * @return the total price
     */
    public Long getTotalPrice() {
        return totalPrice;
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
     * Lifecycle callback for pre-persist.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
        if (creationDate == null) {
            creationDate = new Date();
        }
        calculateTotalPrice();
    }
    
    /**
     * Lifecycle callback for pre-update.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
        calculateTotalPrice();
    }
    
    /**
     * Calculates total price of the order.
     */
    private void calculateTotalPrice() {
        if (orderItems != null) {
            totalPrice = orderItems.stream()
                .mapToLong(item -> item.getBook() != null ? item.getBook().getPrice() : 0L)
                .sum();
        }
    }
    
    /**
     * Helper method to add order item.
     * 
     * @param book the book to add
     */
    public void addOrderItem(final Book book) {
        OrderItem item = new OrderItem();
        item.setOrder(this);
        item.setBook(book);
        orderItems.add(item);
    }
    
    /**
     * Checks if the order contains out of stock books.
     * 
     * @return true if contains out of stock books
     */
    public boolean containsOutOfStockBooks() {
        return getBooks().stream()
            .anyMatch(book -> book.getStatus() == Book.BookStatus.OUT_OF_STOCK);
    }
    
    /**
     * Gets the list of out of stock books in the order.
     * 
     * @return list of out of stock books
     */
    public List<Book> getOutOfStockBooks() {
        return getBooks().stream()
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
            id, status, getTotalPrice(), getBooks().size());
    }
    
    /**
     * Converts the order to CSV format.
     * 
     * @return CSV string representation
     */
    public String toCSV() {
        String booksList = getBooks().stream()
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
            String[] parts = csvLine.split(",", INDEX_5);
            if (parts.length < INDEX_5) {
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
            
            OrderStatus statusValue = OrderStatus.valueOf(parts[2]);
            Date creationDateValue = new Date(Long.parseLong(parts[3]));
            Date completionDateValue = parts[4].isEmpty() 
                ? null 
                : new Date(Long.parseLong(parts[4]));
            
            return new Order(orderId, bookList, statusValue, 
                           creationDateValue, completionDateValue);
            
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
package bookstore.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedQueries;
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

import bookstore.exception.CSVImportException;

/**
 * Represents a book in the bookstore inventory.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Entity
@Table(name = "books")
@NamedQueries({
    @NamedQuery(
        name = "Book.findByIsbn",
        query = "SELECT b FROM Book b WHERE b.isbn = :isbn"
    ),
    @NamedQuery(
        name = "Book.findByStatus",
        query = "SELECT b FROM Book b WHERE b.status = :status ORDER BY b.title"
    ),
    @NamedQuery(
        name = "Book.findByOrderId",
        query = "SELECT b FROM Book b JOIN b.orderItems oi WHERE oi.order.id = :orderId"
    )
})
public class Book implements Serializable {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Default timestamp value for missing dates. */
    private static final long DEFAULT_TIMESTAMP = 0L;
    
    /** Magic number 3 for array access. */
    private static final int INDEX_3 = 3;
    
    /** Magic number 4 for array access. */
    private static final int INDEX_4 = 4;
    
    /** Magic number 5 for array access. */
    private static final int INDEX_5 = 5;
    
    /** Magic number 6 for array access. */
    private static final int INDEX_6 = 6;
    
    /** Magic number 7 for array access. */
    private static final int INDEX_7 = 7;
    
    /** Magic number 8 for array access. */
    private static final int INDEX_8 = 8;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "isbn", unique = true, nullable = false, length = 20)
    private String isbn;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "author", nullable = false, length = 255)
    private String author;
    
    @Column(name = "price", nullable = false)
    private Long price;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookStatus status;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "publication_date")
    private Date publicationDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "arrival_date")
    private Date arrivalDate;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    
    /**
     * Enumeration of possible book statuses.
     */
    public enum BookStatus {
        /** Book is in stock and available. */
        IN_STOCK,
        
        /** Book is out of stock. */
        OUT_OF_STOCK;
        
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
    public Book() {
        this.status = BookStatus.IN_STOCK;
    }
    
    /**
     * Constructs a new book without ID.
     * 
     * @param isbnValue the ISBN
     * @param titleValue the title
     * @param authorValue the author
     * @param priceValue the price
     * @param publicationDateValue the publication date
     * @param arrivalDateValue the arrival date
     * @param descriptionValue the description
     */
    public Book(final String isbnValue, final String titleValue, final String authorValue,
                 final Long priceValue, final Date publicationDateValue,
                 final Date arrivalDateValue, final String descriptionValue) {
        this.isbn = isbnValue;
        this.title = titleValue;
        this.author = authorValue;
        this.price = priceValue;
        this.status = BookStatus.IN_STOCK;
        this.publicationDate = publicationDateValue;
        this.arrivalDate = arrivalDateValue;
        this.description = descriptionValue;
    }
    
    /**
     * Constructs a new book with all fields.
     * 
     * @param idValue the ID
     * @param isbnValue the ISBN
     * @param titleValue the title
     * @param authorValue the author
     * @param priceValue the price
     * @param statusValue the status
     * @param publicationDateValue the publication date
     * @param arrivalDateValue the arrival date
     * @param descriptionValue the description
     */
    public Book(final Integer idValue, final String isbnValue, final String titleValue,
                 final String authorValue, final Long priceValue, final BookStatus statusValue,
                 final Date publicationDateValue, final Date arrivalDateValue,
                 final String descriptionValue) {
        this.id = idValue;
        this.isbn = isbnValue;
        this.title = titleValue;
        this.author = authorValue;
        this.price = priceValue;
        this.status = statusValue;
        this.publicationDate = publicationDateValue;
        this.arrivalDate = arrivalDateValue;
        this.description = descriptionValue;
    }

    /**
     * Gets the book ID.
     * 
     * @return the ID
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Sets the book ID.
     * 
     * @param idValue the new ID
     */
    public void setId(final Integer idValue) {
        this.id = idValue;
    }
    
    /**
     * Gets the ISBN.
     * 
     * @return the ISBN
     */
    public String getIsbn() {
        return isbn;
    }
    
    /**
     * Sets the ISBN.
     * 
     * @param isbnValue the new ISBN
     */
    public void setIsbn(final String isbnValue) {
        this.isbn = isbnValue;
    }
    
    /**
     * Gets the title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the title.
     * 
     * @param titleValue the new title
     */
    public void setTitle(final String titleValue) {
        this.title = titleValue;
    }
    
    /**
     * Gets the author.
     * 
     * @return the author
     */
    public String getAuthor() {
        return author;
    }
    
    /**
     * Sets the author.
     * 
     * @param authorValue the new author
     */
    public void setAuthor(final String authorValue) {
        this.author = authorValue;
    }
    
    /**
     * Gets the price.
     * 
     * @return the price
     */
    public Long getPrice() {
        return price;
    }
    
    /**
     * Sets the price.
     * 
     * @param priceValue the new price
     */
    public void setPrice(final Long priceValue) {
        this.price = priceValue;
    }
    
    /**
     * Gets the status.
     * 
     * @return the status
     */
    public BookStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the status.
     * 
     * @param statusValue the new status
     */
    public void setStatus(final BookStatus statusValue) {
        this.status = statusValue;
    }
    
    /**
     * Gets the publication date.
     * 
     * @return the publication date
     */
    public Date getPublicationDate() {
        return publicationDate;
    }
    
    /**
     * Sets the publication date.
     * 
     * @param publicationDateValue the new publication date
     */
    public void setPublicationDate(final Date publicationDateValue) {
        this.publicationDate = publicationDateValue;
    }
    
    /**
     * Gets the arrival date.
     * 
     * @return the arrival date
     */
    public Date getArrivalDate() {
        return arrivalDate;
    }
    
    /**
     * Sets the arrival date.
     * 
     * @param arrivalDateValue the new arrival date
     */
    public void setArrivalDate(final Date arrivalDateValue) {
        this.arrivalDate = arrivalDateValue;
    }
    
    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description.
     * 
     * @param descriptionValue the new description
     */
    public void setDescription(final String descriptionValue) {
        this.description = descriptionValue;
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
     * Gets the order items.
     * 
     * @return list of order items
     */
    public List<OrderItem> getOrderItems() {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
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
    
    @Override
    public String toString() {
        return String.format(
            "ID: %d, ISBN: %s, Title: %s, Author: %s, Price: %d, Status: %s", 
            id, isbn, title, author, price, status);
    }
    
    /**
     * Converts the book to CSV format.
     * 
     * @return CSV string representation
     */
    public String toCSV() {
        String pubDate = publicationDate != null 
            ? String.valueOf(publicationDate.getTime()) 
            : String.valueOf(DEFAULT_TIMESTAMP);
        String arrDate = arrivalDate != null 
            ? String.valueOf(arrivalDate.getTime()) 
            : String.valueOf(DEFAULT_TIMESTAMP);
        String desc = description != null 
            ? description.replace(",", "\\,") 
            : "";
            
        return String.format("%d,%s,%s,%s,%d,%s,%s,%s,%s",
                id != null ? id : 0,
                isbn != null ? isbn : "",
                title != null ? title : "",
                author != null ? author : "",
                price != null ? price : 0L,
                status != null ? status : BookStatus.IN_STOCK,
                pubDate,
                arrDate,
                desc);
    }
    
    /**
     * Creates a book from CSV line.
     * 
     * @param csvLine the CSV line
     * @return the created book
     * @throws CSVImportException if parsing fails
     */
    public static Book fromCSV(final String csvLine) throws CSVImportException {
        try {
            String[] parts = parseCSVLine(csvLine);
            if (parts.length < INDEX_8 + 1) {
                throw new CSVImportException(
                    "Недостаточно данных для книги: " + csvLine);
            }
            
            Integer idValue = parts[0].isEmpty() ? null : Integer.parseInt(parts[0]);
            String isbnValue = parts[1];
            String titleValue = parts[2];
            String authorValue = parts[INDEX_3];
            Long priceValue = Long.parseLong(parts[INDEX_4]);
            BookStatus statusValue = BookStatus.valueOf(parts[INDEX_5]);
            Date publicationDateValue = new Date(Long.parseLong(parts[INDEX_6]));
            Date arrivalDateValue = new Date(Long.parseLong(parts[INDEX_7]));
            String descriptionValue = parts[INDEX_8].replace("\\,", ",");
            
            return new Book(idValue, isbnValue, titleValue, authorValue, priceValue, 
                           statusValue, publicationDateValue, arrivalDateValue, 
                           descriptionValue);
        } catch (Exception e) {
            throw new CSVImportException(
                "Ошибка парсинга книги: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parses a CSV line handling quotes.
     * 
     * @param line the CSV line
     * @return array of parsed values
     */
    private static String[] parseCSVLine(final String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
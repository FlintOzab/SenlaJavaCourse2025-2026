package model;

import exception.CSVImportException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Represents a book in the bookstore inventory.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class Book implements Serializable {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    
    /** Default timestamp value for missing dates. */
    private static final long DEFAULT_TIMESTAMP = 0L;
    
    /**
     * Enumeration of possible book statuses.
     */
    public enum BookStatus {
        /** Book is in stock and available. */
        IN_STOCK,
        
        /** Book is out of stock. */
        OUT_OF_STOCK
    }
    
    /** Book ID. */
    private Integer id;
    
    /** Book ISBN. */
    private String isbn;
    
    /** Book title. */
    private String title;
    
    /** Book author. */
    private String author;
    
    /** Book price. */
    private Long price;
    
    /** Book status. */
    private BookStatus status;
    
    /** Book publication date. */
    private Date publicationDate;
    
    /** Book arrival date. */
    private Date arrivalDate;
    
    /** Book description. */
    private String description;
    
    /** Creation timestamp. */
    private Date createdAt;
    
    /** Last update timestamp. */
    private Date updatedAt;
    
    /**
     * Default constructor.
     */
    public Book() {
    }
    
    /**
     * Constructs a new book without ID.
     * 
     * @param isbn the ISBN
     * @param title the title
     * @param author the author
     * @param price the price
     * @param publicationDate the publication date
     * @param arrivalDate the arrival date
     * @param description the description
     */
    public Book(final String isbn, final String title, final String author,
                 final Long price, final Date publicationDate,
                 final Date arrivalDate, final String description) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.status = BookStatus.IN_STOCK;
        this.publicationDate = publicationDate;
        this.arrivalDate = arrivalDate;
        this.description = description;
    }
    
    /**
     * Constructs a new book with all fields.
     * 
     * @param id the ID
     * @param isbn the ISBN
     * @param title the title
     * @param author the author
     * @param price the price
     * @param status the status
     * @param publicationDate the publication date
     * @param arrivalDate the arrival date
     * @param description the description
     */
    public Book(final Integer id, final String isbn, final String title,
                 final String author, final Long price, final BookStatus status,
                 final Date publicationDate, final Date arrivalDate,
                 final String description) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.status = status;
        this.publicationDate = publicationDate;
        this.arrivalDate = arrivalDate;
        this.description = description;
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
     * @param id the new ID
     */
    public void setId(final Integer id) {
        this.id = id;
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
     * @param isbn the new ISBN
     */
    public void setIsbn(final String isbn) {
        this.isbn = isbn;
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
     * @param title the new title
     */
    public void setTitle(final String title) {
        this.title = title;
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
     * @param author the new author
     */
    public void setAuthor(final String author) {
        this.author = author;
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
     * @param price the new price
     */
    public void setPrice(final Long price) {
        this.price = price;
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
     * @param status the new status
     */
    public void setStatus(final BookStatus status) {
        this.status = status;
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
     * @param publicationDate the new publication date
     */
    public void setPublicationDate(final Date publicationDate) {
        this.publicationDate = publicationDate;
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
     * @param arrivalDate the new arrival date
     */
    public void setArrivalDate(final Date arrivalDate) {
        this.arrivalDate = arrivalDate;
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
     * @param description the new description
     */
    public void setDescription(final String description) {
        this.description = description;
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
            if (parts.length < 9) {
                throw new CSVImportException(
                    "Недостаточно данных для книги: " + csvLine);
            }
            
            Integer id = parts[0].isEmpty() ? null : Integer.parseInt(parts[0]);
            String isbn = parts[1];
            String title = parts[2];
            String author = parts[3];
            Long price = Long.parseLong(parts[4]);
            BookStatus status = BookStatus.valueOf(parts[5]);
            Date publicationDate = new Date(Long.parseLong(parts[6]));
            Date arrivalDate = new Date(Long.parseLong(parts[7]));
            String description = parts[8].replace("\\,", ",");
            
            return new Book(id, isbn, title, author, price, status, 
                           publicationDate, arrivalDate, description);
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
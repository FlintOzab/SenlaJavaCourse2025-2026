package t4;

import java.util.Date;

class Book {
    private static int bookCounter = 0;
    
    enum BookStatus {
        IN_STOCK,
        OUT_OF_STOCK
    }
    
    private String isbn;
    private String title;
    private String author;
    private Long price;
    private BookStatus status;
    private Date publicationDate;
    private Date arrivalDate;
    private String description;
    
 
    public Book(String isbn, String title, String author, Long price, 
                Date publicationDate, Date arrivalDate, String description) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.status = BookStatus.IN_STOCK;
        this.publicationDate = publicationDate;
        this.arrivalDate = arrivalDate;
        this.description = description;
        bookCounter++;
    }
    
    public String getIsbn() { return this.isbn; }
    public String getTitle() { return this.title; }
    public String getAuthor() { return this.author; }
    public Long getPrice() { return this.price; }
    public BookStatus getStatus() { return this.status; }
    public Date getPublicationDate() { return publicationDate; }
    public Date getArrivalDate() { return arrivalDate; }
    public String getDescription() { return this.description; }
    
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPrice(Long price) { this.price = price; }
    public void setStatus(BookStatus status) { this.status = status; }
    public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }
    public void setArrivalDate(Date arrivalDate) { this.arrivalDate = arrivalDate; }
}

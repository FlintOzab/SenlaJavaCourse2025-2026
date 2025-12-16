package t4;

public class Book {
	private static int bookCounter = 0;
	
	enum BookStatus {
	    IN_STOCK,
	    OUT_OF_STOCK
	}
    private String isbn;
    private String title;
    private String author;
    private long price;
    private BookStatus status;
    public Book(String isbn, String title, String author, long price) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.status = BookStatus.IN_STOCK;
        bookCounter++;
    }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPrice(long price) { this.price = price; }
    public void setStatus(BookStatus status) { this.status = status; }
    
    public String getIsbn() { return this.isbn; }
    public String getTitle() { return this.title; }
    public String getAuthor() { return this.author; }
    public long getPrice() { return this.price; }
    public BookStatus getStatus() { return this.status; }
    
    
}

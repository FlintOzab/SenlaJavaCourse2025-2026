package t4;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Book {
    private static int bookCounter = 0;
    
    public enum BookStatus {
        IN_STOCK,
        OUT_OF_STOCK
    }
    
    private int id;
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
        this.id = bookCounter++;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.status = BookStatus.IN_STOCK;
        this.publicationDate = publicationDate;
        this.arrivalDate = arrivalDate;
        this.description = description;
    }
    
    public Book(int id, String isbn, String title, String author, Long price, 
                BookStatus status, Date publicationDate, Date arrivalDate, String description) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.status = status;
        this.publicationDate = publicationDate;
        this.arrivalDate = arrivalDate;
        this.description = description;
        bookCounter = Math.max(bookCounter, id + 1);
    }
    
    public int getId() { return id; }
    public String getIsbn() { return this.isbn; }
    public String getTitle() { return this.title; }
    public String getAuthor() { return this.author; }
    public Long getPrice() { return this.price; }
    public BookStatus getStatus() { return this.status; }
    public Date getPublicationDate() { return publicationDate; }
    public Date getArrivalDate() { return arrivalDate; }
    public String getDescription() { return this.description; }
    
    public void setId(int id) { this.id = id; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPrice(Long price) { this.price = price; }
    public void setStatus(BookStatus status) { this.status = status; }
    public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }
    public void setArrivalDate(Date arrivalDate) { this.arrivalDate = arrivalDate; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return String.format("ID: %d, ISBN: %s, Title: %s, Author: %s, Price: %d, Status: %s", 
                           id, isbn, title, author, price, status);
    }
    
    public String toCSV() {
        return String.format("%d,%s,%s,%s,%d,%s,%d,%d,%s",
                id, isbn, title, author, price, status,
                publicationDate.getTime(), arrivalDate.getTime(),
                description != null ? description.replace(",", "\\,") : "");
    }
    
    public static Book fromCSV(String csvLine) throws CSVImportException {
        try {
            String[] parts = parseCSVLine(csvLine);
            if (parts.length < 9) {
                throw new CSVImportException("Недостаточно данных для книги: " + csvLine);
            }
            
            int id = Integer.parseInt(parts[0]);
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
            throw new CSVImportException("Ошибка парсинга книги: " + e.getMessage(), e);
        }
    }
    
    private static String[] parseCSVLine(String line) {
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
}
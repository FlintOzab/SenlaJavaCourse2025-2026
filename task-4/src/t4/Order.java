package t4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import t4.Book.BookStatus;

class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    enum OrderStatus {
        NEW,
        COMPLETED,
        CANCELLED
    }
       
    private int orderId;
    private List<Book> books;
    private OrderStatus status;
    private Date creationDate;
    private Date completionDate;
    
    public Order(int orderId, List<Book> books) {
        this.orderId = orderId;
        this.books = new ArrayList<>(books);
        this.status = OrderStatus.NEW;
        this.creationDate = new Date();
        this.completionDate = null;
    }
    
    public Order(int orderId, List<Book> books, OrderStatus status, 
                 Date creationDate, Date completionDate) {
        this.orderId = orderId;
        this.books = new ArrayList<>(books);
        this.status = status;
        this.creationDate = creationDate;
        this.completionDate = completionDate;
    }
    
    public int getOrderId() { return orderId; }
    public List<Book> getBooks() { return new ArrayList<>(this.books); }
    public OrderStatus getStatus() { return this.status; }
    public Date getCreationDate() { return this.creationDate; }
    public Date getCompletionDate() { return this.completionDate; }
    public void setCompletionDate(Date date) { this.completionDate = date; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setBooks(List<Book> books) { this.books = new ArrayList<>(books); }
    
    public long getTotalPrice() {
        return books.stream().mapToLong(Book::getPrice).sum();
    }
    
    public boolean containsOutOfStockBooks() {
        return books.stream().anyMatch(book -> book.getStatus() == Book.BookStatus.OUT_OF_STOCK);
    }
    
    public List<Book> getOutOfStockBooks() {
        return books.stream()
            .filter(book -> book.getStatus() == Book.BookStatus.OUT_OF_STOCK)
            .collect(Collectors.toList());
    }
    
    public void completeOrder() {
        if (!containsOutOfStockBooks()) {
            this.status = OrderStatus.COMPLETED;
            this.completionDate = new Date();
        }
    }
    
    public void cancelOrder() {
        this.status = OrderStatus.CANCELLED;
    }
    
    @Override
    public String toString() {
        return String.format("Заказ #%d - Статус: %s - Сумма: %d руб. - Книг: %d", 
                           orderId, status, getTotalPrice(), books.size());
    }
    
    public String toCSV() {
        String booksList = books.stream()
                .map(Book::toCSV)
                .collect(Collectors.joining(";"));
        return String.format("%d,%s,%s,%s,%s",
                orderId, booksList, status,
                creationDate.getTime(),
                completionDate != null ? String.valueOf(completionDate.getTime()) : "");
    }

    public static Order fromCSV(String csvLine) throws CSVImportException {
        try {
            String[] parts = csvLine.split(",");
            if (parts.length < 5) {
                throw new CSVImportException("Недостаточно данных для заказа: " + csvLine);
            }
            
            int orderId = Integer.parseInt(parts[0]);
            
            List<Book> bookList = new ArrayList<>();
            
            StringBuilder booksCSVBuilder = new StringBuilder(parts[1]);
            String[] bookCSVStrings = parts[1].split(";");
            
            for (String bookCSV : bookCSVStrings) {
                if (!bookCSV.trim().isEmpty()) {
                    try {
                        Book book = Book.fromCSV(bookCSV);
                        bookList.add(book);
                    } catch (CSVImportException e) {
                        throw new CSVImportException("Ошибка парсинга книги в заказе: " + e.getMessage(), e);
                    }
                }
            }
            
            OrderStatus status = OrderStatus.valueOf(parts[2]);
            Date creationDate = new Date(Long.parseLong(parts[3]));
            Date completionDate = parts[4].isEmpty() ? null : new Date(Long.parseLong(parts[4]));
            
            return new Order(orderId, bookList, status, creationDate, completionDate);
            
        } catch (Exception e) {
            throw new CSVImportException("Ошибка парсинга заказа: " + e.getMessage(), e);
        }
    }
}

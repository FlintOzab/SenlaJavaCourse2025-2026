package t4.control;
import java.util.Date;
import java.util.List;

import di.annotation.Component;
import di.annotation.Inject;
import t4.config.BookstoreConfig;
import t4.exception.BookstoreException;
import t4.exception.EntityNotFoundException;
import t4.exception.ValidationException;
import t4.model.Book;
import t4.model.Order;
import t4.service.Bookstore;
import t4.service.StateManager;

@Component
public class BookstoreController {
    private final Bookstore bookstore;
    private final StateManager stateManager;
    
    @Inject
    public BookstoreController(Bookstore bookstore, StateManager stateManager) {
        this.bookstore = bookstore;
        this.stateManager = stateManager;
    }
   
    public void saveState() throws BookstoreException {
        stateManager.saveState();
    }
    
    public void createBackup() throws BookstoreException {
        stateManager.createBackup();
    }
    
    public void addBook(Book book) throws BookstoreException {
        try {
            bookstore.addBookToInventory(book);
        } catch (ValidationException e) {
            throw new BookstoreException("Ошибка добавления книги: " + e.getMessage(), e);
        }
    }
    
    public void writeOffBook(String isbn) throws BookstoreException {
        try {
            bookstore.writeOffBook(isbn);
        } catch (EntityNotFoundException e) {
            throw new BookstoreException("Книга не найдена: " + e.getMessage(), e);
        }
    }
    
    public Order createOrder(int orderNumber, List<Integer> bookIds) throws BookstoreException {
        try {
            return bookstore.createOrder(orderNumber, bookIds);
        } catch (ValidationException e) {
            throw new BookstoreException("Ошибка создания заказа: " + e.getMessage(), e);
        }
    }
    
    public void cancelOrder(int orderId) throws BookstoreException {
        try {
            bookstore.cancelOrder(orderId);
        } catch (EntityNotFoundException | ValidationException e) {
            throw new BookstoreException("Ошибка отмены заказа: " + e.getMessage(), e);
        }
    }
    
    public void completeOrder(int orderId) throws BookstoreException {
        try {
            bookstore.completeOrder(orderId);
        } catch (EntityNotFoundException | ValidationException e) {
            throw new BookstoreException("Ошибка завершения заказа: " + e.getMessage(), e);
        }
    }
    
    public void updateOrderStatus(int orderId, Order.OrderStatus status) throws BookstoreException {
        try {
            bookstore.updateOrderStatus(orderId, status);
        } catch (EntityNotFoundException | ValidationException e) {
            throw new BookstoreException("Ошибка обновления статуса заказа: " + e.getMessage(), e);
        }
    }
    
    public List<Book> getAllBooks() {
        return bookstore.getBookInventory();
    }
    
    public List<Order> getAllOrders() {
        return bookstore.getAllOrders();
    }
    
    public String getBookDetails(String isbn) {
        return bookstore.getBookDetails(isbn);
    }
    
    public String getOrderDetails(int orderId) {
        return bookstore.getOrderDetails(orderId);
    }
    
    public List<Book> getOldBooks() {
        return bookstore.getOldBooks();
    }
    public BookstoreConfig getConfig() {
    	return bookstore.getConfig(); 
    }
    
    public Bookstore getBookstore() {
        return bookstore;
    }
    
    public long getTotalRevenueInPeriod(Date startDate, Date endDate) {
        return bookstore.getTotalRevenueInPeriod(startDate, endDate);
    }
    
    public int getCompletedOrdersCountInPeriod(Date startDate, Date endDate) {
        return bookstore.getCompletedOrdersCountInPeriod(startDate, endDate);
    }
    
    public List<Order> getCompletedOrdersInPeriod(Date startDate, Date endDate) {
        return bookstore.getCompletedOrdersInPeriod(startDate, endDate);
    }
    
    public void saveAllData() throws BookstoreException {
        bookstore.saveAllData();
    }
    
    public void exportBooksToCSV(String filePath) throws BookstoreException {
        bookstore.exportBooksToCSV(filePath);
    }
    
    public void importBooksFromCSV(String filePath) throws BookstoreException {
        bookstore.importBooksFromCSV(filePath);
    }
    
    public void exportOrdersToCSV(String filePath) throws BookstoreException {
        bookstore.exportOrdersToCSV(filePath);
    }
    
    public void importOrdersFromCSV(String filePath) throws BookstoreException {
        bookstore.importOrdersFromCSV(filePath);
    }
}

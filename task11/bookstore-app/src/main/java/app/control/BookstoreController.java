package app.control;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import config.BookstoreConfig;
import exception.BookstoreException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import model.Book;
import model.Order;
import service.Bookstore;
import service.StateManager;
import util.LoggerUtil;

/**
 * Controller for handling bookstore operations.
 * Provides an interface between the view and the business logic.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class BookstoreController {
    
    /** Logger instance for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BookstoreController.class);
    
    /** The bookstore service instance. */
    private final Bookstore bookstore;
    
    /** The state manager instance. */
    private final StateManager stateManager;
    
    /**
     * Constructs a new BookstoreController with the specified dependencies.
     * 
     * @param bookstore the bookstore service
     * @param stateManager the state manager
     */
    @Autowired
    public BookstoreController(final Bookstore bookstore, final StateManager stateManager) {
        this.bookstore = bookstore;
        this.stateManager = stateManager;
        LOGGER.info("BookstoreController initialized");
    }
   
    /**
     * Saves the current state of the application.
     * 
     * @throws BookstoreException if an error occurs during save
     */
    public void saveState() throws BookstoreException {
        LoggerUtil.logCommandStart(LOGGER, "saveState");
        try {
            stateManager.saveState();
            LoggerUtil.logCommandSuccess(LOGGER, "saveState", null);
        } catch (BookstoreException e) {
            LoggerUtil.logCommandError(LOGGER, "saveState", e);
            throw e;
        }
    }
    
    /**
     * Creates a backup of the current state.
     * 
     * @throws BookstoreException if an error occurs during backup creation
     */
    public void createBackup() throws BookstoreException {
        LoggerUtil.logCommandStart(LOGGER, "createBackup");
        try {
            stateManager.createBackup();
            LoggerUtil.logCommandSuccess(LOGGER, "createBackup", null);
        } catch (BookstoreException e) {
            LoggerUtil.logCommandError(LOGGER, "createBackup", e);
            throw e;
        }
    }
    
    /**
     * Adds a new book to the inventory.
     * 
     * @param book the book to add
     * @throws BookstoreException if an error occurs during addition
     */
    public void addBook(final Book book) throws BookstoreException {
        LoggerUtil.logCommandStart(LOGGER, "addBook", book.getIsbn(), book.getTitle());
        try {
            bookstore.addBook(book);
            LoggerUtil.logCommandSuccess(LOGGER, "addBook", book.getTitle());
        } catch (ValidationException e) {
            LoggerUtil.logCommandError(LOGGER, "addBook", e);
            throw new BookstoreException("Ошибка добавления книги: " + e.getMessage(), e);
        }
    }
    
    /**
     * Writes off a book from the inventory by ISBN.
     * 
     * @param isbn the ISBN of the book to write off
     * @throws BookstoreException if the book is not found or an error occurs
     */
    public void writeOffBook(final String isbn) throws BookstoreException {
        LoggerUtil.logCommandStart(LOGGER, "writeOffBook", isbn);
        try {
            bookstore.writeOffBook(isbn);
            LoggerUtil.logCommandSuccess(LOGGER, "writeOffBook", isbn);
        } catch (EntityNotFoundException e) {
            LoggerUtil.logCommandError(LOGGER, "writeOffBook", e);
            throw new BookstoreException("Книга не найдена: " + e.getMessage(), e);
        }
    }
    
    /**
     * Creates a new order with the specified book IDs.
     * 
     * @param bookIds the list of book IDs to include in the order
     * @return the created order
     * @throws BookstoreException if an error occurs during order creation
     */
    public Order createOrder(final List<Integer> bookIds) throws BookstoreException {
        LoggerUtil.logCommandStart(LOGGER, "createOrder", bookIds);
        try {
            Order order = bookstore.createOrder(bookIds);
            LoggerUtil.logCommandSuccess(LOGGER, "createOrder", order.getId());
            return order;
        } catch (ValidationException e) {
            LoggerUtil.logCommandError(LOGGER, "createOrder", e);
            throw new BookstoreException("Ошибка создания заказа: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cancels an order by its ID.
     * 
     * @param orderId the ID of the order to cancel
     * @throws BookstoreException if the order is not found or cannot be cancelled
     */
    public void cancelOrder(final Integer orderId) throws BookstoreException {
        LoggerUtil.logCommandStart(LOGGER, "cancelOrder", orderId);
        try {
            bookstore.cancelOrder(orderId);
            LoggerUtil.logCommandSuccess(LOGGER, "cancelOrder", orderId);
        } catch (EntityNotFoundException | ValidationException e) {
            LoggerUtil.logCommandError(LOGGER, "cancelOrder", e);
            throw new BookstoreException("Ошибка отмены заказа: " + e.getMessage(), e);
        }
    }
    
    /**
     * Completes an order by its ID.
     * 
     * @param orderId the ID of the order to complete
     * @throws BookstoreException if the order is not found or cannot be completed
     */
    public void completeOrder(final Integer orderId) throws BookstoreException {
        LoggerUtil.logCommandStart(LOGGER, "completeOrder", orderId);
        try {
            bookstore.completeOrder(orderId);
            LoggerUtil.logCommandSuccess(LOGGER, "completeOrder", orderId);
        } catch (EntityNotFoundException | ValidationException e) {
            LoggerUtil.logCommandError(LOGGER, "completeOrder", e);
            throw new BookstoreException("Ошибка завершения заказа: " + e.getMessage(), e);
        }
    }
    
    /**
     * Updates the status of an order.
     * 
     * @param orderId the ID of the order
     * @param status the new status
     * @throws BookstoreException if the order is not found or status update fails
     */
    public void updateOrderStatus(final Integer orderId, final Order.OrderStatus status) 
            throws BookstoreException {
        LoggerUtil.logCommandStart(LOGGER, "updateOrderStatus", orderId, status);
        try {
            bookstore.updateOrderStatus(orderId, status);
            LoggerUtil.logCommandSuccess(LOGGER, "updateOrderStatus", orderId);
        } catch (EntityNotFoundException | ValidationException e) {
            LoggerUtil.logCommandError(LOGGER, "updateOrderStatus", e);
            throw new BookstoreException("Ошибка обновления статуса заказа: " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns a list of all books in the inventory.
     * 
     * @return list of all books
     */
    public List<Book> getAllBooks() {
        LOGGER.debug("Getting all books");
        return bookstore.getAllBooks();
    }
    
    /**
     * Returns a list of all orders.
     * 
     * @return list of all orders
     */
    public List<Order> getAllOrders() {
        LOGGER.debug("Getting all orders");
        return bookstore.getAllOrders();
    }
    
    /**
     * Returns details of a book by its ISBN.
     * 
     * @param isbn the ISBN of the book
     * @return string with book details
     */
    public String getBookDetails(final String isbn) {
        LOGGER.debug("Getting book details for ISBN: {}", isbn);
        return bookstore.getBookDetails(isbn);
    }
    
    /**
     * Returns details of an order by its ID.
     * 
     * @param orderId the ID of the order
     * @return string with order details
     */
    public String getOrderDetails(final Integer orderId) {
        LOGGER.debug("Getting order details for ID: {}", orderId);
        return bookstore.getOrderDetails(orderId);
    }
    
    /**
     * Returns a list of old books (without recent sales).
     * 
     * @return list of old books
     */
    public List<Book> getOldBooks() {
        LOGGER.debug("Getting old books");
        return bookstore.getOldBooks();
    }
    
    /**
     * Returns the bookstore configuration.
     * 
     * @return the bookstore configuration
     */
    public BookstoreConfig getConfig() {
        return bookstore.getConfig();
    }
    
    /**
     * Returns the underlying bookstore instance.
     * 
     * @return the bookstore instance
     */
    public Bookstore getBookstore() {
        return bookstore;
    }
    
    /**
     * Calculates total revenue for a specified period.
     * 
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return total revenue in the period
     */
    public long getTotalRevenueInPeriod(final Date startDate, final Date endDate) {
        LOGGER.debug("Getting total revenue for period: {} - {}", startDate, endDate);
        return bookstore.getTotalRevenueInPeriod(startDate, endDate);
    }
    
    /**
     * Returns the count of completed orders in a specified period.
     * 
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return count of completed orders
     */
    public int getCompletedOrdersCountInPeriod(final Date startDate, final Date endDate) {
        LOGGER.debug("Getting completed orders count for period: {} - {}", startDate, endDate);
        return bookstore.getCompletedOrdersCountInPeriod(startDate, endDate);
    }
    
    /**
     * Returns a list of completed orders in a specified period.
     * 
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return list of completed orders
     */
    public List<Order> getCompletedOrdersInPeriod(final Date startDate, final Date endDate) {
        LOGGER.debug("Getting completed orders for period: {} - {}", startDate, endDate);
        return bookstore.getCompletedOrdersInPeriod(startDate, endDate);
    }
    
    /**
     * Saves all data to persistent storage.
     * 
     * @throws BookstoreException if an error occurs during save
     */
    public void saveAllData() throws BookstoreException {
        LoggerUtil.logCommandStart(LOGGER, "saveAllData");
        try {
            bookstore.saveAllData();
            LoggerUtil.logCommandSuccess(LOGGER, "saveAllData", null);
        } catch (BookstoreException e) {
            LoggerUtil.logCommandError(LOGGER, "saveAllData", e);
            throw e;
        }
    }
    
    /**
     * Exports books to a CSV file.
     * 
     * @param filePath the path to the CSV file
     * @throws BookstoreException if an error occurs during export
     */
    public void exportBooksToCSV(final String filePath) throws BookstoreException {
        LoggerUtil.logCommandStart(LOGGER, "exportBooksToCSV", filePath);
        try {
            bookstore.exportBooksToCSV(filePath);
            LoggerUtil.logCommandSuccess(LOGGER, "exportBooksToCSV", filePath);
        } catch (BookstoreException e) {
            LoggerUtil.logCommandError(LOGGER, "exportBooksToCSV", e);
            throw e;
        }
    }
    
    /**
     * Imports books from a CSV file.
     * 
     * @param filePath the path to the CSV file
     * @throws BookstoreException if an error occurs during import
     */
    public void importBooksFromCSV(final String filePath) throws BookstoreException {
        LoggerUtil.logCommandStart(LOGGER, "importBooksFromCSV", filePath);
        try {
            bookstore.importBooksFromCSV(filePath);
            LoggerUtil.logCommandSuccess(LOGGER, "importBooksFromCSV", filePath);
        } catch (BookstoreException e) {
            LoggerUtil.logCommandError(LOGGER, "importBooksFromCSV", e);
            throw e;
        }
    }
    
    /**
     * Exports orders to a CSV file.
     * 
     * @param filePath the path to the CSV file
     * @throws BookstoreException if an error occurs during export
     */
    public void exportOrdersToCSV(final String filePath) throws BookstoreException {
        LoggerUtil.logCommandStart(LOGGER, "exportOrdersToCSV", filePath);
        try {
            bookstore.exportOrdersToCSV(filePath);
            LoggerUtil.logCommandSuccess(LOGGER, "exportOrdersToCSV", filePath);
        } catch (BookstoreException e) {
            LoggerUtil.logCommandError(LOGGER, "exportOrdersToCSV", e);
            throw e;
        }
    }
    
    /**
     * Imports orders from a CSV file.
     * 
     * @param filePath the path to the CSV file
     * @throws BookstoreException if an error occurs during import
     */
    public void importOrdersFromCSV(final String filePath) throws BookstoreException {
        LoggerUtil.logCommandStart(LOGGER, "importOrdersFromCSV", filePath);
        try {
            bookstore.importOrdersFromCSV(filePath);
            LoggerUtil.logCommandSuccess(LOGGER, "importOrdersFromCSV", filePath);
        } catch (BookstoreException e) {
            LoggerUtil.logCommandError(LOGGER, "importOrdersFromCSV", e);
            throw e;
        }
    }
}
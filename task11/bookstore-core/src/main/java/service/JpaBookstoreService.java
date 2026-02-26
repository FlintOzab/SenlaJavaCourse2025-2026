package service;

import dao.jpa.JpaBookDAOInterface;
import dao.jpa.JpaDAOFactory;
import dao.jpa.JpaOrderDAOInterface;
import dao.jpa.JpaRequestDAOInterface;
import exception.BookstoreException;
import exception.ValidationException;
import jpa.JpaTransactionManager;
import model.Book;
import model.Book.BookStatus;
import model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * JPA-based implementation of bookstore business logic.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Service
public class JpaBookstoreService {
    
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaBookstoreService.class);
    
    /** JPA transaction manager. */
    private final JpaTransactionManager transactionManager;
    
    /** Book DAO. */
    private final JpaBookDAOInterface bookDAO;
    
    /** Order DAO. */
    private final JpaOrderDAOInterface orderDAO;
    
    /** Request DAO. */
    private final JpaRequestDAOInterface requestDAO;
    
    /**
     * Constructs a new JpaBookstoreService with the specified dependencies.
     * 
     * @param daoFactory the JPA DAO factory
     * @param transactionManager the JPA transaction manager
     */
    @Autowired
    public JpaBookstoreService(final JpaDAOFactory daoFactory,
                                final JpaTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.bookDAO = daoFactory.getBookDAO();
        this.orderDAO = daoFactory.getOrderDAO();
        this.requestDAO = daoFactory.getRequestDAO();
        LOGGER.info("JpaBookstoreService initialized");
    }
    
    /**
     * Adds a new book with JPA transaction management.
     * 
     * @param book the book to add
     * @return the saved book
     * @throws BookstoreException if validation fails
     */
    public Book addBook(final Book book) throws BookstoreException {
        validateBook(book);
        
        return transactionManager.executeInTransaction(em -> {
            Optional<Book> existingBook = bookDAO.findByIsbn(book.getIsbn());
            if (existingBook.isPresent()) {
                throw new ValidationException(
                    "Book with ISBN " + book.getIsbn() + " already exists");
            }
            
            Book savedBook = bookDAO.save(book);
            LOGGER.info("Book added successfully with ID: {}", savedBook.getId());
            return savedBook;
        });
    }
    
    /**
     * Finds books by status using JPA.
     * 
     * @param status the status
     * @return list of books
     */
    public List<Book> findBooksByStatus(final BookStatus status) {
        LOGGER.debug("Finding books by status: {}", status);
        return bookDAO.findByStatus(status);
    }
    
    /**
     * Gets order statistics for a period using JPA aggregation.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return revenue statistics
     */
    public long getRevenueInPeriod(final Date startDate, final Date endDate) {
        LOGGER.debug("Getting revenue for period: {} - {}", startDate, endDate);
        return orderDAO.getTotalRevenueInPeriod(startDate, endDate);
    }
    
    /**
     * Finds completed orders in period using Criteria API.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of completed orders
     */
    public List<Order> findCompletedOrdersInPeriod(final Date startDate, final Date endDate) {
        LOGGER.debug("Finding completed orders in period: {} - {}", startDate, endDate);
        return orderDAO.findCompletedInPeriod(startDate, endDate);
    }
    
    /**
     * Finds active requests using named query.
     * 
     * @return list of active requests
     */
    public List<model.Request> findActiveRequests() {
        LOGGER.debug("Finding active requests");
        return requestDAO.findActiveRequests();
    }
    
    /**
     * Marks requests as done for an order using bulk update.
     * 
     * @param orderId the order ID
     * @return number of updated requests
     */
    public int completeRequestsForOrder(final Integer orderId) {
        LOGGER.debug("Completing requests for order: {}", orderId);
        return requestDAO.markRequestsAsDoneForOrder(orderId);
    }
    
    /**
     * Finds books with pagination.
     * 
     * @param page the page number
     * @param size the page size
     * @return list of books
     */
    public List<Book> findBooksWithPagination(final int page, final int size) {
        LOGGER.debug("Finding books with pagination - page: {}, size: {}", page, size);
        return bookDAO.findWithPagination(page, size);
    }
    
    /**
     * Gets total book count.
     * 
     * @return total count
     */
    public long getTotalBookCount() {
        LOGGER.debug("Getting total book count");
        return bookDAO.count();
    }
    
    /**
     * Validates a book.
     * 
     * @param book the book to validate
     * @throws ValidationException if validation fails
     */
    private void validateBook(final Book book) throws ValidationException {
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new ValidationException("Book ISBN cannot be empty");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new ValidationException("Book title cannot be empty");
        }
        if (book.getPrice() == null || book.getPrice() < 0) {
            throw new ValidationException("Book price must be non-negative");
        }
    }
}
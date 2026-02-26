package service;

import dao.jpa.JpaBookDAOInterface;
import dao.jpa.JpaDAOFactory;
import dao.jpa.JpaOrderDAOInterface;
import dao.jpa.JpaRequestDAOInterface;
import config.BookstoreConfig;
import exception.BookstoreException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import model.Book;
import model.Book.BookStatus;
import model.Order;
import model.Order.OrderStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Core business logic for the bookstore application.
 * Manages books, orders, and related operations using JPA.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class Bookstore {
    
    /** Book DAO. */
    private final JpaBookDAOInterface bookDAO;
    
    /** Order DAO. */
    private final JpaOrderDAOInterface orderDAO;
    
    /** Request DAO. */
    private final JpaRequestDAOInterface requestDAO;
    
    /** Bookstore configuration. */
    private final BookstoreConfig config;
    
    /** Milliseconds in a day for date calculations. */
    private static final long MILLIS_IN_DAY = 1000L * 60 * 60 * 24;
    
    /** Days in a month approximation. */
    private static final long DAYS_IN_MONTH = 30L;
   
    /**
     * Constructs a new Bookstore with the specified dependencies.
     * 
     * @param daoFactory the JPA DAO factory
     * @param config the bookstore configuration
     */
    @Autowired
    public Bookstore(JpaDAOFactory daoFactory,
                     BookstoreConfig config) {
        this.bookDAO = daoFactory.getBookDAO();
        this.orderDAO = daoFactory.getOrderDAO();
        this.requestDAO = daoFactory.getRequestDAO();
        this.config = config;
    }
    
    /**
     * Adds a new book to the inventory.
     * 
     * @param book the book to add
     * @return the saved book with generated ID
     * @throws BookstoreException if validation fails or book already exists
     */
    @Transactional
    public Book addBook(final Book book) throws BookstoreException {
        validateBook(book);
        
        Optional<Book> existingBook = bookDAO.findByIsbn(book.getIsbn());
        if (existingBook.isPresent()) {
            throw new ValidationException(
                "Книга с ISBN " + book.getIsbn() + " уже существует");
        }
        
        return bookDAO.save(book);
    }
    
    /**
     * Writes off a book from inventory.
     * 
     * @param isbn the ISBN of the book to write off
     * @throws BookstoreException if book not found
     */
    @Transactional
    public void writeOffBook(final String isbn) throws BookstoreException {
        Book book = bookDAO.findByIsbn(isbn)
            .orElseThrow(() -> new EntityNotFoundException(
                "Книга с ISBN " + isbn + " не найдена"));
        
        book.setStatus(BookStatus.OUT_OF_STOCK);
        bookDAO.update(book);
    }
    
    /**
     * Creates a new order.
     * 
     * @param bookIds the IDs of books to include
     * @return the created order
     * @throws BookstoreException if validation fails
     */
    @Transactional
    public Order createOrder(final List<Integer> bookIds) throws BookstoreException {
        if (bookIds == null || bookIds.isEmpty()) {
            throw new ValidationException("Заказ должен содержать хотя бы одну книгу");
        }
        
        List<Book> books = bookIds.stream()
            .map(id -> {
				try {
					return bookDAO.findById(id)
					    .orElseThrow(() -> new EntityNotFoundException(
					        "Книга с ID " + id + " не найдена"));
				} catch (EntityNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			})
            .toList();
        
        Order order = new Order(books);
        return orderDAO.save(order);
    }
    
    /**
     * Completes an order.
     * 
     * @param orderId the ID of the order to complete
     * @throws BookstoreException if order not found or cannot be completed
     */
    @Transactional
    public void completeOrder(final Integer orderId) throws BookstoreException {
        Order order = orderDAO.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Заказ с ID " + orderId + " не найден"));
        
        if (order.getStatus() != OrderStatus.NEW) {
            throw new ValidationException("Можно завершать только новые заказы");
        }
        
        if (order.containsOutOfStockBooks()) {
            throw new ValidationException(
                "Невозможно завершить заказ с отсутствующими книгами");
        }
        
        order.completeOrder();
        orderDAO.update(order);
        
        // Mark associated requests as done
        requestDAO.markRequestsAsDoneForOrder(orderId);
    }
    
    /**
     * Cancels an order.
     * 
     * @param orderId the ID of the order to cancel
     * @throws BookstoreException if order not found or cannot be cancelled
     */
    @Transactional
    public void cancelOrder(final Integer orderId) throws BookstoreException {
        Order order = orderDAO.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Заказ с ID " + orderId + " не найден"));
        
        if (order.getStatus() != OrderStatus.NEW) {
            throw new ValidationException("Можно отменять только новые заказы");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        orderDAO.update(order);
    }
    
    /**
     * Updates an order's status.
     * 
     * @param orderId the ID of the order
     * @param status the new status
     * @throws BookstoreException if order not found
     */
    @Transactional
    public void updateOrderStatus(final Integer orderId, 
                                   final OrderStatus status) throws BookstoreException {
        Order order = orderDAO.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Заказ с ID " + orderId + " не найден"));
        
        order.setStatus(status);
        if (status == OrderStatus.COMPLETED) {
            order.setCompletionDate(new Date());
            // Mark associated requests as done
            requestDAO.markRequestsAsDoneForOrder(orderId);
        }
        orderDAO.update(order);
    }
    
    /**
     * Gets all books.
     * 
     * @return list of all books
     */
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }
    
    /**
     * Gets all orders.
     * 
     * @return list of all orders
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }
    
    /**
     * Finds a book by ISBN.
     * 
     * @param isbn the ISBN
     * @return optional containing the book if found
     */
    @Transactional(readOnly = true)
    public Optional<Book> findBookByIsbn(final String isbn) {
        return bookDAO.findByIsbn(isbn);
    }
    
    /**
     * Finds an order by ID.
     * 
     * @param id the order ID
     * @return optional containing the order if found
     */
    @Transactional(readOnly = true)
    public Optional<Order> findOrderById(final Integer id) {
        return orderDAO.findById(id);
    }
    
    /**
     * Gets formatted book details.
     * 
     * @param isbn the ISBN
     * @return formatted book details
     */
    @Transactional(readOnly = true)
    public String getBookDetails(final String isbn) {
        Optional<Book> bookOpt = bookDAO.findByIsbn(isbn);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            StringBuilder sb = new StringBuilder();
            sb.append("Информация о книге:\n");
            sb.append("ID: ").append(book.getId()).append("\n");
            sb.append("ISBN: ").append(book.getIsbn()).append("\n");
            sb.append("Название: ").append(book.getTitle()).append("\n");
            sb.append("Автор: ").append(book.getAuthor()).append("\n");
            sb.append("Цена: ").append(book.getPrice()).append(" руб.\n");
            sb.append("Статус: ").append(book.getStatus()).append("\n");
            sb.append("Дата публикации: ").append(book.getPublicationDate()).append("\n");
            sb.append("Дата поступления: ").append(book.getArrivalDate()).append("\n");
            return sb.toString();
        } else {
            return "Книга с ISBN " + isbn + " не найдена";
        }
    }
    
    /**
     * Gets formatted order details.
     * 
     * @param orderId the order ID
     * @return formatted order details
     */
    @Transactional(readOnly = true)
    public String getOrderDetails(final Integer orderId) {
        Optional<Order> orderOpt = orderDAO.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            StringBuilder sb = new StringBuilder();
            sb.append("Заказ #").append(order.getId()).append("\n");
            sb.append("Статус: ").append(order.getStatus()).append("\n");
            sb.append("Дата создания: ").append(order.getCreationDate()).append("\n");
            if (order.getCompletionDate() != null) {
                sb.append("Дата выполнения: ").append(order.getCompletionDate()).append("\n");
            }
            sb.append("Общая стоимость: ").append(order.calculateTotalPrice()).append(" руб.\n");
            sb.append("Книги в заказе:\n");
            order.getBooks().forEach(book -> 
                sb.append(" - ").append(book.getTitle()).append(" (")
                  .append(book.getAuthor()).append(") - ")
                  .append(book.getPrice()).append(" руб.\n")
            );
            return sb.toString();
        } else {
            return "Заказ #" + orderId + " не найден";
        }
    }
    
    /**
     * Gets old books that haven't been sold for a while.
     * 
     * @return list of old books
     */
    @Transactional(readOnly = true)
    public List<Book> getOldBooks() {
        return bookDAO.findAll().stream()
            .filter(book -> {
                if (book.getArrivalDate() == null) {
                    return false;
                }
                long diffInMillies = new Date().getTime() - book.getArrivalDate().getTime();
                long diffInMonths = diffInMillies / (MILLIS_IN_DAY * DAYS_IN_MONTH);
                return diffInMonths >= config.getStaleMonthsThreshold();
            })
            .toList();
    }
    
    /**
     * Calculates total revenue in a period.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return the total revenue
     */
    @Transactional(readOnly = true)
    public long getTotalRevenueInPeriod(final Date startDate, final Date endDate) {
        return orderDAO.getTotalRevenueInPeriod(startDate, endDate);
    }
    
    /**
     * Counts completed orders in a period.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return the count
     */
    @Transactional(readOnly = true)
    public int getCompletedOrdersCountInPeriod(final Date startDate, final Date endDate) {
        return (int) orderDAO.countCompletedInPeriod(startDate, endDate);
    }
    
    /**
     * Gets completed orders in a period.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of completed orders
     */
    @Transactional(readOnly = true)
    public List<Order> getCompletedOrdersInPeriod(final Date startDate, final Date endDate) {
        return orderDAO.findCompletedInPeriod(startDate, endDate);
    }
    
    /**
     * Saves all data to CSV files.
     * 
     * @throws BookstoreException if save fails
     */
    @Transactional(readOnly = true)
    public void saveAllData() throws BookstoreException {
        // Implementation for saving all data if needed
    }
    
    /**
     * Exports books to CSV file.
     * 
     * @param filePath the file path
     * @throws BookstoreException if export fails
     */
    @Transactional(readOnly = true)
    public void exportBooksToCSV(final String filePath) throws BookstoreException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            List<Book> books = bookDAO.findAll();
            for (Book book : books) {
                writer.println(book.toCSV());
            }
        } catch (IOException e) {
            throw new BookstoreException("Ошибка экспорта книг: " + e.getMessage(), e);
        }
    }
    
    /**
     * Imports books from CSV file.
     * 
     * @param filePath the file path
     * @throws BookstoreException if import fails
     */
    @Transactional
    public void importBooksFromCSV(final String filePath) throws BookstoreException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    Book book = Book.fromCSV(line);
                    bookDAO.save(book);
                }
            }
        } catch (Exception e) {
            throw new BookstoreException("Ошибка импорта книг: " + e.getMessage(), e);
        }
    }
    
    /**
     * Exports orders to CSV file.
     * 
     * @param filePath the file path
     * @throws BookstoreException if export fails
     */
    @Transactional(readOnly = true)
    public void exportOrdersToCSV(final String filePath) throws BookstoreException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            List<Order> orders = orderDAO.findAll();
            for (Order order : orders) {
                writer.println(order.toCSV());
            }
        } catch (IOException e) {
            throw new BookstoreException("Ошибка экспорта заказов: " + e.getMessage(), e);
        }
    }
    
    /**
     * Imports orders from CSV file.
     * 
     * @param filePath the file path
     * @throws BookstoreException if import fails
     */
    @Transactional
    public void importOrdersFromCSV(final String filePath) throws BookstoreException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    Order order = Order.fromCSV(line);
                    orderDAO.save(order);
                }
            }
        } catch (Exception e) {
            throw new BookstoreException("Ошибка импорта заказов: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets the bookstore configuration.
     * 
     * @return the configuration
     */
    public BookstoreConfig getConfig() {
        return config;
    }
    
    /**
     * Validates a book.
     * 
     * @param book the book to validate
     * @throws ValidationException if validation fails
     */
    private void validateBook(final Book book) throws ValidationException {
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new ValidationException("ISBN книги не может быть пустым");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new ValidationException("Название книги не может быть пустым");
        }
        if (book.getPrice() == null || book.getPrice() < 0) {
            throw new ValidationException("Цена книги должна быть неотрицательной");
        }
    }
}
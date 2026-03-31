package bookstore.service;

import bookstore.model.Book;
import bookstore.model.Book.BookStatus;
import bookstore.model.Order;
import bookstore.model.Order.OrderStatus;
import bookstore.model.OrderItem;
import bookstore.model.Request;
import bookstore.model.User;
import bookstore.model.UserRole;
import bookstore.test.common.TestConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating test data objects.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public final class TestDataFactory {
    
    private TestDataFactory() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * Creates a test book with default values.
     * 
     * @return test book
     */
    public static Book createTestBook() {
        Book book = new Book();
        book.setId(1);
        book.setIsbn(TestConstants.TEST_ISBN);
        book.setTitle(TestConstants.TEST_TITLE);
        book.setAuthor(TestConstants.TEST_AUTHOR);
        book.setPrice(TestConstants.TEST_PRICE);
        book.setStatus(BookStatus.IN_STOCK);
        book.setPublicationDate(TestConstants.TEST_DATE);
        book.setArrivalDate(TestConstants.TEST_DATE);
        book.setDescription(TestConstants.TEST_DESCRIPTION);
        return book;
    }
    
    /**
     * Creates a test book with custom ID.
     * 
     * @param id the book ID
     * @return test book
     */
    public static Book createTestBook(int id) {
        Book book = createTestBook();
        book.setId(id);
        return book;
    }
    
    /**
     * Creates a test book with custom values.
     * 
     * @param id the book ID
     * @param isbn the ISBN
     * @param title the title
     * @param author the author
     * @param price the price
     * @return test book
     */
    public static Book createTestBook(int id, String isbn, String title, 
                                       String author, long price) {
        Book book = new Book();
        book.setId(id);
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(price);
        book.setStatus(BookStatus.IN_STOCK);
        book.setPublicationDate(TestConstants.TEST_DATE);
        book.setArrivalDate(TestConstants.TEST_DATE);
        return book;
    }
    
    /**
     * Creates a test order with default values.
     * 
     * @return test order
     */
    public static Order createTestOrder() {
        Order order = new Order();
        order.setId(1);
        order.setStatus(OrderStatus.NEW);
        order.setCreationDate(TestConstants.TEST_DATE);
        order.setOrderItems(new ArrayList<>());
        
        Book book = createTestBook();
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setBook(book);
        order.getOrderItems().add(orderItem);
        
        return order;
    }
    
    /**
     * Creates a test order with custom ID.
     * 
     * @param id the order ID
     * @return test order
     */
    public static Order createTestOrder(int id) {
        Order order = createTestOrder();
        order.setId(id);
        return order;
    }
    
    /**
     * Creates a test order with specific books.
     * 
     * @param id the order ID
     * @param books the list of books
     * @return test order
     */
    public static Order createTestOrder(int id, List<Book> books) {
        Order order = createTestOrder(id);
        order.setOrderItems(new ArrayList<>());
        for (Book book : books) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            order.getOrderItems().add(orderItem);
        }
        return order;
    }
    
    /**
     * Creates a completed test order.
     * 
     * @param id the order ID
     * @return completed test order
     */
    public static Order createCompletedTestOrder(int id) {
        Order order = createTestOrder(id);
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletionDate(TestConstants.TEST_DATE);
        return order;
    }
    
    /**
     * Creates a test request.
     * 
     * @param id the request ID
     * @param order the associated order
     * @param book the associated book
     * @param done whether request is done
     * @return test request
     */
    public static Request createTestRequest(int id, Order order, Book book, boolean done) {
        Request request = new Request();
        request.setId(id);
        request.setOrder(order);
        request.setBook(book);
        request.setDone(done);
        return request;
    }
    
    /**
     * Creates a list of test books.
     * 
     * @param count number of books to create
     * @return list of books
     */
    public static List<Book> createTestBooks(int count) {
        List<Book> books = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            books.add(createTestBook(i, 
                "978-5-17-123456-" + i,
                "Test Book " + i,
                "Test Author " + i,
                500L * i));
        }
        return books;
    }
    
    /**
     * Creates a test user.
     * 
     * @param id the user ID
     * @param username the username
     * @param role the user role
     * @return test user
     */
    public static User createTestUser(int id, String username, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword("password");
        user.setRole(role);
        user.setEmail(username + "@test.com");
        user.setEnabled(true);
        return user;
    }
    
    /**
     * Creates an admin test user.
     * 
     * @return admin user
     */
    public static User createTestAdmin() {
        return createTestUser(1, "admin", UserRole.ADMIN);
    }
    
    /**
     * Creates a regular test user.
     * 
     * @return regular user
     */
    public static User createTestUser() {
        return createTestUser(2, "user", UserRole.USER);
    }
}
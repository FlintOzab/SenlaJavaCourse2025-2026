package bookstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookstore.config.BookstoreConfig;
import bookstore.dao.jpa.JpaBookDAOInterface;
import bookstore.dao.jpa.JpaDAOFactory;
import bookstore.dao.jpa.JpaOrderDAOInterface;
import bookstore.dao.jpa.JpaRequestDAOInterface;
import bookstore.exception.EntityNotFoundException;
import bookstore.exception.ValidationException;
import bookstore.model.Book;
import bookstore.model.Book.BookStatus;
import bookstore.model.Order;
import bookstore.model.Order.OrderStatus;
import bookstore.test.common.BaseTest;
import bookstore.test.common.TestConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BookstoreTest extends BaseTest {

    @Mock
    private JpaDAOFactory daoFactory;

    @Mock
    private JpaBookDAOInterface bookDAO;

    @Mock
    private JpaOrderDAOInterface orderDAO;

    @Mock
    private JpaRequestDAOInterface requestDAO;

    @Mock
    private BookstoreConfig config;

    private Bookstore bookstore;

    private Book testBook;
    private Order testOrder;
    @BeforeEach
    public void setUp() {
    	MockitoAnnotations.openMocks(this);
    	
        testBook = TestDataFactory.createTestBook();
        testOrder = TestDataFactory.createTestOrder();
        when(daoFactory.getBookDAO()).thenReturn(bookDAO);
        when(daoFactory.getOrderDAO()).thenReturn(orderDAO);
        when(daoFactory.getRequestDAO()).thenReturn(requestDAO);
        
        bookstore = new Bookstore(daoFactory, config);
        logInfo("Test setup completed for BookstoreTest");
        logDebug("bookDAO mock: {}", bookDAO);
        logDebug("orderDAO mock: {}", orderDAO);
        logDebug("requestDAO mock: {}", requestDAO);
    }

    // ==================== addBook Tests ====================

    @Test
    void testAddBook_Success() {
        when(bookDAO.findByIsbn(anyString())).thenReturn(Optional.empty());
        when(bookDAO.save(any(Book.class))).thenReturn(testBook);

        Book result = assertDoesNotThrow(() -> bookstore.addBook(testBook));

        assertNotNull(result);
        assertEquals(testBook.getId(), result.getId());
        assertEquals(testBook.getIsbn(), result.getIsbn());
        verify(bookDAO).save(any(Book.class));
    }

    @Test
    void testAddBook_DuplicateIsbn_ThrowsValidationException() {
        when(bookDAO.findByIsbn(anyString())).thenReturn(Optional.of(testBook));

        ValidationException exception = assertThrows(ValidationException.class,
            () -> bookstore.addBook(testBook));

        assertTrue(exception.getMessage().contains("уже существует"));
        verify(bookDAO, never()).save(any(Book.class));
    }

    @Test
    void testAddBook_NullIsbn_ThrowsValidationException() {
        testBook.setIsbn(null);

        ValidationException exception = assertThrows(ValidationException.class,
            () -> bookstore.addBook(testBook));

        assertTrue(exception.getMessage().contains("не может быть пустым"));
        verify(bookDAO, never()).save(any(Book.class));
    }

    @Test
    void testAddBook_NegativePrice_ThrowsValidationException() {
        testBook.setPrice(-100L);

        ValidationException exception = assertThrows(ValidationException.class,
            () -> bookstore.addBook(testBook));

        assertTrue(exception.getMessage().contains("Цена книги должна быть неотрицательной"));
        verify(bookDAO, never()).save(any(Book.class));
    }

    // ==================== writeOffBook Tests ====================

    @Test
    void testWriteOffBook_Success() {
        when(bookDAO.findByIsbn(anyString())).thenReturn(Optional.of(testBook));
        when(bookDAO.update(any(Book.class))).thenReturn(testBook);

        assertDoesNotThrow(() -> bookstore.writeOffBook(TestConstants.TEST_ISBN));

        assertEquals(BookStatus.OUT_OF_STOCK, testBook.getStatus());
        verify(bookDAO).update(testBook);
    }

    @Test
    void testWriteOffBook_BookNotFound_ThrowsEntityNotFoundException() {
        when(bookDAO.findByIsbn(anyString())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> bookstore.writeOffBook(TestConstants.TEST_ISBN));

        assertTrue(exception.getMessage().contains("не найдена"));
        verify(bookDAO, never()).update(any(Book.class));
    }

    // ==================== createOrder Tests ====================

    @Test
    void testCreateOrder_Success() {
        when(bookDAO.findById(anyInt())).thenReturn(Optional.of(testBook));
        when(orderDAO.save(any(Order.class))).thenReturn(testOrder);

        List<Integer> bookIds = Arrays.asList(1);

        Order result = assertDoesNotThrow(() -> bookstore.createOrder(bookIds));

        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        verify(orderDAO).save(any(Order.class));
    }

    @Test
    void testCreateOrder_EmptyBookList_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
            () -> bookstore.createOrder(Arrays.asList()));

        assertTrue(exception.getMessage().contains("хотя бы одну книгу"));
        verify(orderDAO, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrder_BookNotFound_ThrowsEntityNotFoundException() {
        when(bookDAO.findById(anyInt())).thenReturn(Optional.empty());

        List<Integer> bookIds = Arrays.asList(999);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> bookstore.createOrder(bookIds));

        assertTrue(exception.getMessage().contains("не найдена"));
        verify(orderDAO, never()).save(any(Order.class));
    }

    // ==================== completeOrder Tests ====================

    @Test
    void testCompleteOrder_Success() {
        when(orderDAO.findById(anyInt())).thenReturn(Optional.of(testOrder));
        when(orderDAO.update(any(Order.class))).thenReturn(testOrder);
        when(requestDAO.markRequestsAsDoneForOrder(anyInt())).thenReturn(1);

        assertDoesNotThrow(() -> bookstore.completeOrder(1));

        assertEquals(OrderStatus.COMPLETED, testOrder.getStatus());
        assertNotNull(testOrder.getCompletionDate());
        verify(orderDAO).update(testOrder);
        verify(requestDAO).markRequestsAsDoneForOrder(1);
    }

    @Test
    void testCompleteOrder_OrderNotFound_ThrowsEntityNotFoundException() {
        when(orderDAO.findById(anyInt())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> bookstore.completeOrder(999));

        assertTrue(exception.getMessage().contains("не найден"));
        verify(orderDAO, never()).update(any(Order.class));
    }

    @Test
    void testCompleteOrder_AlreadyCompleted_ThrowsValidationException() {
        testOrder.setStatus(OrderStatus.COMPLETED);
        when(orderDAO.findById(anyInt())).thenReturn(Optional.of(testOrder));

        ValidationException exception = assertThrows(ValidationException.class,
            () -> bookstore.completeOrder(1));

        assertTrue(exception.getMessage().contains("только новые заказы"));
        verify(orderDAO, never()).update(any(Order.class));
    }

    // ==================== cancelOrder Tests ====================

    @Test
    void testCancelOrder_Success() {
        when(orderDAO.findById(anyInt())).thenReturn(Optional.of(testOrder));
        when(orderDAO.update(any(Order.class))).thenReturn(testOrder);

        assertDoesNotThrow(() -> bookstore.cancelOrder(1));

        assertEquals(OrderStatus.CANCELLED, testOrder.getStatus());
        verify(orderDAO).update(testOrder);
    }

    @Test
    void testCancelOrder_OrderNotFound_ThrowsEntityNotFoundException() {
        when(orderDAO.findById(anyInt())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> bookstore.cancelOrder(999));

        assertTrue(exception.getMessage().contains("не найден"));
        verify(orderDAO, never()).update(any(Order.class));
    }

    // ==================== getAllBooks Tests ====================

    @Test
    void testGetAllBooks_Success() {
        List<Book> expectedBooks = Arrays.asList(testBook);
        when(bookDAO.findAll()).thenReturn(expectedBooks);

        List<Book> result = bookstore.getAllBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook.getId(), result.get(0).getId());
        verify(bookDAO).findAll();
    }

    // ==================== getAllOrders Tests ====================

    @Test
    void testGetAllOrders_Success() {
        List<Order> expectedOrders = Arrays.asList(testOrder);
        when(orderDAO.findAll()).thenReturn(expectedOrders);

        List<Order> result = bookstore.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder.getId(), result.get(0).getId());
        verify(orderDAO).findAll();
    }

    // ==================== findBookByIsbn Tests ====================

    @Test
    void testFindBookByIsbn_Success() {
        when(bookDAO.findByIsbn(anyString())).thenReturn(Optional.of(testBook));

        Optional<Book> result = bookstore.findBookByIsbn(TestConstants.TEST_ISBN);

        assertTrue(result.isPresent());
        assertEquals(testBook.getId(), result.get().getId());
        verify(bookDAO).findByIsbn(anyString());
    }

    @Test
    void testFindBookByIsbn_NotFound() {
        when(bookDAO.findByIsbn(anyString())).thenReturn(Optional.empty());

        Optional<Book> result = bookstore.findBookByIsbn(TestConstants.TEST_ISBN);

        assertTrue(result.isEmpty());
        verify(bookDAO).findByIsbn(anyString());
    }

    // ==================== getBookDetails Tests ====================

    @Test
    void testGetBookDetails_Success() {
        when(bookDAO.findByIsbn(anyString())).thenReturn(Optional.of(testBook));

        String result = bookstore.getBookDetails(TestConstants.TEST_ISBN);

        assertNotNull(result);
        assertTrue(result.contains("Информация о книге"));
        assertTrue(result.contains(testBook.getTitle()));
        verify(bookDAO).findByIsbn(anyString());
    }

    @Test
    void testGetBookDetails_NotFound() {
        when(bookDAO.findByIsbn(anyString())).thenReturn(Optional.empty());

        String result = bookstore.getBookDetails(TestConstants.TEST_ISBN);

        assertNotNull(result);
        assertTrue(result.contains("не найдена"));
        verify(bookDAO).findByIsbn(anyString());
    }
}
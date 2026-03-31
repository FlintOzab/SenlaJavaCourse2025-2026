package bookstore.app.control;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookstore.config.BookstoreConfig;
import bookstore.exception.BookstoreException;
import bookstore.exception.EntityNotFoundException;
import bookstore.exception.ValidationException;
import bookstore.model.Book;
import bookstore.model.Order;
import bookstore.model.Order.OrderStatus;
import bookstore.service.Bookstore;
import bookstore.service.StateManager;
import bookstore.service.TestDataFactory;
import bookstore.test.common.BaseTest;
import bookstore.test.common.TestConstants;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class BookstoreControllerTest extends BaseTest {

    @Mock
    private Bookstore bookstore;

    @Mock
    private StateManager stateManager;

    @Mock
    private BookstoreConfig config;

    @InjectMocks
    private BookstoreController controller;

    private Book testBook;
    private Order testOrder;
    @BeforeEach
    public void setUp() {
        testBook = TestDataFactory.createTestBook();
        testOrder = TestDataFactory.createTestOrder();
    }

    // ==================== saveState Tests ====================

    @Test
    void testSaveState_Success() throws BookstoreException {
        doNothing().when(stateManager).saveState();

        assertDoesNotThrow(() -> controller.saveState());

        verify(stateManager).saveState();
    }

    @Test
    void testSaveState_Failure_ThrowsBookstoreException() throws BookstoreException {
        doThrow(new BookstoreException("Save failed")).when(stateManager).saveState();

        assertThrows(BookstoreException.class, () -> controller.saveState());

        verify(stateManager).saveState();
    }

    // ==================== addBook Tests ====================

    @Test
    void testAddBook_Success() throws BookstoreException {
        when(bookstore.addBook(any(Book.class))).thenReturn(testBook);

        assertDoesNotThrow(() -> controller.addBook(testBook));

        verify(bookstore).addBook(testBook);
    }

    @Test
    void testAddBook_ValidationError_ThrowsBookstoreException() throws BookstoreException {
        when(bookstore.addBook(any(Book.class)))
            .thenThrow(new ValidationException("Invalid book"));

        BookstoreException exception = assertThrows(BookstoreException.class,
            () -> controller.addBook(testBook));

        assertTrue(exception.getMessage().contains("Ошибка добавления книги"));
        verify(bookstore).addBook(testBook);
    }

    // ==================== writeOffBook Tests ====================

    @Test
    void testWriteOffBook_Success() throws BookstoreException {
        doNothing().when(bookstore).writeOffBook(anyString());

        assertDoesNotThrow(() -> controller.writeOffBook(TestConstants.TEST_ISBN));

        verify(bookstore).writeOffBook(TestConstants.TEST_ISBN);
    }

    @Test
    void testWriteOffBook_NotFound_ThrowsBookstoreException() throws BookstoreException {
        doThrow(new EntityNotFoundException("Book not found"))
            .when(bookstore).writeOffBook(anyString());

        BookstoreException exception = assertThrows(BookstoreException.class,
            () -> controller.writeOffBook(TestConstants.TEST_ISBN));

        assertTrue(exception.getMessage().contains("Книга не найдена"));
        verify(bookstore).writeOffBook(anyString());
    }

    // ==================== createOrder Tests ====================

    @Test
    void testCreateOrder_Success() throws BookstoreException {
        List<Integer> bookIds = Arrays.asList(1);
        when(bookstore.createOrder(any(List.class))).thenReturn(testOrder);

        Order result = controller.createOrder(bookIds);

        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        verify(bookstore).createOrder(bookIds);
    }

    @Test
    void testCreateOrder_ValidationError_ThrowsBookstoreException() throws BookstoreException {
        List<Integer> bookIds = Arrays.asList();
        when(bookstore.createOrder(any(List.class)))
            .thenThrow(new ValidationException("Empty order"));

        BookstoreException exception = assertThrows(BookstoreException.class,
            () -> controller.createOrder(bookIds));

        assertTrue(exception.getMessage().contains("Ошибка создания заказа"));
        verify(bookstore).createOrder(bookIds);
    }

    // ==================== cancelOrder Tests ====================

    @Test
    void testCancelOrder_Success() throws BookstoreException {
        doNothing().when(bookstore).cancelOrder(anyInt());

        assertDoesNotThrow(() -> controller.cancelOrder(1));

        verify(bookstore).cancelOrder(1);
    }

    @Test
    void testCancelOrder_NotFound_ThrowsBookstoreException() throws BookstoreException {
        doThrow(new EntityNotFoundException("Order not found"))
            .when(bookstore).cancelOrder(anyInt());

        BookstoreException exception = assertThrows(BookstoreException.class,
            () -> controller.cancelOrder(1));

        assertTrue(exception.getMessage().contains("Ошибка отмены заказа"));
        verify(bookstore).cancelOrder(1);
    }

    // ==================== completeOrder Tests ====================

    @Test
    void testCompleteOrder_Success() throws BookstoreException {
        doNothing().when(bookstore).completeOrder(anyInt());

        assertDoesNotThrow(() -> controller.completeOrder(1));

        verify(bookstore).completeOrder(1);
    }

    // ==================== updateOrderStatus Tests ====================

    @Test
    void testUpdateOrderStatus_Success() throws BookstoreException {
        doNothing().when(bookstore).updateOrderStatus(anyInt(), any(OrderStatus.class));

        assertDoesNotThrow(() -> controller.updateOrderStatus(1, OrderStatus.COMPLETED));

        verify(bookstore).updateOrderStatus(1, OrderStatus.COMPLETED);
    }

    // ==================== getAllBooks Tests ====================

    @Test
    void testGetAllBooks_Success() {
        List<Book> expectedBooks = Arrays.asList(testBook);
        when(bookstore.getAllBooks()).thenReturn(expectedBooks);

        List<Book> result = controller.getAllBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookstore).getAllBooks();
    }

    // ==================== getAllOrders Tests ====================

    @Test
    void testGetAllOrders_Success() {
        List<Order> expectedOrders = Arrays.asList(testOrder);
        when(bookstore.getAllOrders()).thenReturn(expectedOrders);

        List<Order> result = controller.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookstore).getAllOrders();
    }

    // ==================== getBookDetails Tests ====================

    @Test
    void testGetBookDetails_Success() {
        String expectedDetails = "Book details";
        when(bookstore.getBookDetails(anyString())).thenReturn(expectedDetails);

        String result = controller.getBookDetails(TestConstants.TEST_ISBN);

        assertEquals(expectedDetails, result);
        verify(bookstore).getBookDetails(TestConstants.TEST_ISBN);
    }

    // ==================== getOrderDetails Tests ====================

    @Test
    void testGetOrderDetails_Success() {
        String expectedDetails = "Order details";
        when(bookstore.getOrderDetails(anyInt())).thenReturn(expectedDetails);

        String result = controller.getOrderDetails(1);

        assertEquals(expectedDetails, result);
        verify(bookstore).getOrderDetails(1);
    }

    // ==================== getOldBooks Tests ====================

    @Test
    void testGetOldBooks_Success() {
        List<Book> expectedBooks = Arrays.asList(testBook);
        when(bookstore.getOldBooks()).thenReturn(expectedBooks);

        List<Book> result = controller.getOldBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookstore).getOldBooks();
    }

    // ==================== getConfig Tests ====================

    @Test
    void testGetConfig_Success() {
        when(bookstore.getConfig()).thenReturn(config);

        BookstoreConfig result = controller.getConfig();

        assertNotNull(result);
        assertEquals(config, result);
        verify(bookstore).getConfig();
    }

    // ==================== exportBooksToCSV Tests ====================

    @Test
    void testExportBooksToCSV_Success() throws BookstoreException {
        doNothing().when(bookstore).exportBooksToCSV(anyString());

        assertDoesNotThrow(() -> controller.exportBooksToCSV("books.csv"));

        verify(bookstore).exportBooksToCSV("books.csv");
    }

    // ==================== importBooksFromCSV Tests ====================

    @Test
    void testImportBooksFromCSV_Success() throws BookstoreException {
        doNothing().when(bookstore).importBooksFromCSV(anyString());

        assertDoesNotThrow(() -> controller.importBooksFromCSV("books.csv"));

        verify(bookstore).importBooksFromCSV("books.csv");
    }
}
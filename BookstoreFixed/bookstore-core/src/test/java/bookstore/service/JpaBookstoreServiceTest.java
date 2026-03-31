package bookstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookstore.dao.jpa.JpaBookDAOInterface;
import bookstore.dao.jpa.JpaDAOFactory;
import bookstore.dao.jpa.JpaOrderDAOInterface;
import bookstore.dao.jpa.JpaRequestDAOInterface;
import bookstore.exception.ValidationException;
import bookstore.jpa.JpaTransactionManager;
import bookstore.jpa.JpaTransactionManager.TransactionOperation;
import bookstore.model.Book;
import bookstore.model.Book.BookStatus;
import bookstore.model.Order;
import bookstore.model.Request;
import bookstore.test.common.BaseTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for JpaBookstoreService.
 * Tests book, order, and request operations with mocked DAOs.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
class JpaBookstoreServiceTest extends BaseTest {

    @Mock
    private JpaDAOFactory daoFactory;

    @Mock
    private JpaBookDAOInterface bookDAO;

    @Mock
    private JpaOrderDAOInterface orderDAO;

    @Mock
    private JpaRequestDAOInterface requestDAO;

    @Mock
    private JpaTransactionManager transactionManager;

    private JpaBookstoreService jpaBookstoreService;

    private Book testBook;
    private Order testOrder;
    private Request testRequest;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        initializeTestData();
        configureDaoFactoryMocks();
        configureTransactionManagerMock();
        
        jpaBookstoreService = new JpaBookstoreService(daoFactory, transactionManager);
    }

    private void initializeTestData() {
        testBook = TestDataFactory.createTestBook();
        testOrder = TestDataFactory.createTestOrder();
        testRequest = TestDataFactory.createTestRequest(1, testOrder, testBook, false);
    }

    private void configureDaoFactoryMocks() {
        when(daoFactory.getBookDAO()).thenReturn(bookDAO);
        when(daoFactory.getOrderDAO()).thenReturn(orderDAO);
        when(daoFactory.getRequestDAO()).thenReturn(requestDAO);
    }

    /**
     * Configures transaction manager mock to execute operations immediately.
     * This simulates the transaction boundary without actual database transactions.
     */
    @SuppressWarnings("unchecked")
    private void configureTransactionManagerMock() {
    	lenient().when(transactionManager.executeInTransaction(any(TransactionOperation.class)))
        .thenAnswer(invocation -> {
            TransactionOperation<Object> operation = 
                (TransactionOperation<Object>) invocation.getArgument(0);
            return operation.execute(null);
        });
    }

    // ==================== addBook Tests ====================

    @Test
    void testAddBook_Success() {
        // Given
        when(bookDAO.findByIsbn(anyString())).thenReturn(Optional.empty());
        when(bookDAO.save(any(Book.class))).thenReturn(testBook);

        // When
        Book result = assertDoesNotThrow(() -> jpaBookstoreService.addBook(testBook));

        // Then
        assertNotNull(result);
        assertEquals(testBook.getId(), result.getId());
        assertEquals(testBook.getIsbn(), result.getIsbn());
        assertEquals(testBook.getTitle(), result.getTitle());
        verify(bookDAO).findByIsbn(testBook.getIsbn());
        verify(bookDAO).save(any(Book.class));
    }

    @Test
    void testAddBook_DuplicateIsbn_ThrowsValidationException() {
        // Given
        when(bookDAO.findByIsbn(anyString())).thenReturn(Optional.of(testBook));

        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> jpaBookstoreService.addBook(testBook)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(bookDAO).findByIsbn(testBook.getIsbn());
        verify(bookDAO, never()).save(any(Book.class));
    }

    // ==================== findBooksByStatus Tests ====================

    @Test
    void testFindBooksByStatus_Success() {
        // Given
        List<Book> expectedBooks = Arrays.asList(testBook);
        when(bookDAO.findByStatus(BookStatus.IN_STOCK)).thenReturn(expectedBooks);

        // When
        List<Book> result = jpaBookstoreService.findBooksByStatus(BookStatus.IN_STOCK);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook.getId(), result.get(0).getId());
        assertEquals(testBook.getIsbn(), result.get(0).getIsbn());
        verify(bookDAO).findByStatus(BookStatus.IN_STOCK);
    }

    @Test
    void testFindBooksByStatus_EmptyList() {
        // Given
        when(bookDAO.findByStatus(BookStatus.OUT_OF_STOCK)).thenReturn(Arrays.asList());

        // When
        List<Book> result = jpaBookstoreService.findBooksByStatus(BookStatus.OUT_OF_STOCK);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookDAO).findByStatus(BookStatus.OUT_OF_STOCK);
    }

    // ==================== findActiveRequests Tests ====================

    @Test
    void testFindActiveRequests_Success() {
        // Given
        List<Request> expectedRequests = Arrays.asList(testRequest);
        when(requestDAO.findActiveRequests()).thenReturn(expectedRequests);

        // When
        List<Request> result = jpaBookstoreService.findActiveRequests();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRequest.getId(), result.get(0).getId());
        verify(requestDAO).findActiveRequests();
    }

    @Test
    void testFindActiveRequests_EmptyList() {
        // Given
        when(requestDAO.findActiveRequests()).thenReturn(Arrays.asList());

        // When
        List<Request> result = jpaBookstoreService.findActiveRequests();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(requestDAO).findActiveRequests();
    }

    // ==================== completeRequestsForOrder Tests ====================

    @Test
    void testCompleteRequestsForOrder_Success() {
        // Given
        int orderId = 1;
        int expectedCount = 3;
        when(requestDAO.markRequestsAsDoneForOrder(orderId)).thenReturn(expectedCount);

        // When
        int result = jpaBookstoreService.completeRequestsForOrder(orderId);

        // Then
        assertEquals(expectedCount, result);
        verify(requestDAO).markRequestsAsDoneForOrder(orderId);
    }

    @Test
    void testCompleteRequestsForOrder_NoRequests() {
        // Given
        int orderId = 999;
        int expectedCount = 0;
        when(requestDAO.markRequestsAsDoneForOrder(orderId)).thenReturn(expectedCount);

        // When
        int result = jpaBookstoreService.completeRequestsForOrder(orderId);

        // Then
        assertEquals(expectedCount, result);
        verify(requestDAO).markRequestsAsDoneForOrder(orderId);
    }

    // ==================== findBooksWithPagination Tests ====================

    @Test
    void testFindBooksWithPagination_Success() {
        // Given
        int offset = 0;
        int limit = 10;
        List<Book> expectedBooks = Arrays.asList(testBook);
        when(bookDAO.findWithPagination(offset, limit)).thenReturn(expectedBooks);

        // When
        List<Book> result = jpaBookstoreService.findBooksWithPagination(offset, limit);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook.getId(), result.get(0).getId());
        verify(bookDAO).findWithPagination(offset, limit);
    }

    @Test
    void testFindBooksWithPagination_EmptyResult() {
        // Given
        int offset = 100;
        int limit = 10;
        when(bookDAO.findWithPagination(offset, limit)).thenReturn(Arrays.asList());

        // When
        List<Book> result = jpaBookstoreService.findBooksWithPagination(offset, limit);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookDAO).findWithPagination(offset, limit);
    }

    @Test
    void testFindBooksWithPagination_InvalidParameters() {
        // Given
        int offset = -1;
        int limit = 10;
        when(bookDAO.findWithPagination(offset, limit)).thenReturn(Arrays.asList());

        // When
        List<Book> result = jpaBookstoreService.findBooksWithPagination(offset, limit);

        // Then
        assertNotNull(result);
        verify(bookDAO).findWithPagination(offset, limit);
    }

    // ==================== getTotalBookCount Tests ====================

    @Test
    void testGetTotalBookCount_Success() {
        // Given
        long expectedCount = 25L;
        when(bookDAO.count()).thenReturn(expectedCount);

        // When
        long result = jpaBookstoreService.getTotalBookCount();

        // Then
        assertEquals(expectedCount, result);
        verify(bookDAO).count();
    }

    @Test
    void testGetTotalBookCount_Zero() {
        // Given
        long expectedCount = 0L;
        when(bookDAO.count()).thenReturn(expectedCount);

        // When
        long result = jpaBookstoreService.getTotalBookCount();

        // Then
        assertEquals(expectedCount, result);
        verify(bookDAO).count();
    }
}
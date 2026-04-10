package bookstore.test.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Common test constants for all test classes.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public final class TestConstants {
    
    private TestConstants() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    // Test data constants
    public static final String TEST_ISBN = "978-5-17-123456-7";
    public static final String TEST_ISBN_2 = "978-5-17-987654-3";
    public static final String TEST_TITLE = "Test Book";
    public static final String TEST_TITLE_2 = "Another Test Book";
    public static final String TEST_AUTHOR = "Test Author";
    public static final String TEST_AUTHOR_2 = "Another Author";
    public static final long TEST_PRICE = 500L;
    public static final long TEST_PRICE_2 = 750L;
    public static final String TEST_DESCRIPTION = "Test Description";
    
    // Date format for tests
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
    
    // Test dates
    public static final Date TEST_DATE = new Date();
    public static final Date TEST_DATE_PAST;
    
    static {
        try {
            TEST_DATE_PAST = DATE_FORMATTER.parse("01.01.2020");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // File paths
    public static final String TEST_EXPORT_DIR = "./target/test-exports/";
    public static final String TEST_BOOKS_CSV = "test-books.csv";
    public static final String TEST_ORDERS_CSV = "test-orders.csv";
    
    // Timeouts
    public static final long TIMEOUT_SECONDS = 5L;
    
    // Configuration test data
    public static final int TEST_STALE_THRESHOLD = 3;
    public static final boolean TEST_AUTO_FULFILL = true;
    public static final long TEST_DEFAULT_PRICE = 100L;
    public static final int TEST_MAX_BOOKS = 5;
}
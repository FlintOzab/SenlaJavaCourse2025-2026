package jpa;

/**
 * Constants for JPA operations.
 * Contains named queries and entity configurations.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public final class JpaConstants {
    
    /** Named query for finding book by ISBN. */
    public static final String QUERY_BOOK_FIND_BY_ISBN = "Book.findByIsbn";
    
    /** Named query for finding books by status. */
    public static final String QUERY_BOOK_FIND_BY_STATUS = "Book.findByStatus";
    
    /** Named query for finding books by order ID. */
    public static final String QUERY_BOOK_FIND_BY_ORDER_ID = "Book.findByOrderId";
    
    /** Named query for finding orders by status. */
    public static final String QUERY_ORDER_FIND_BY_STATUS = "Order.findByStatus";
    
    /** Named query for finding completed orders in period. */
    public static final String QUERY_ORDER_FIND_COMPLETED_IN_PERIOD = "Order.findCompletedInPeriod";
    
    /** Named query for finding requests by order ID. */
    public static final String QUERY_REQUEST_FIND_BY_ORDER_ID = "Request.findByOrderId";
    
    /** Named query for finding requests by book ID. */
    public static final String QUERY_REQUEST_FIND_BY_BOOK_ID = "Request.findByBookId";
    
    /** Named query for finding active requests. */
    public static final String QUERY_REQUEST_FIND_ACTIVE = "Request.findActive";
    
    /** Entity manager factory name. */
    public static final String PERSISTENCE_UNIT_NAME = "bookstorePU";
    
    /** Parameter name for ISBN. */
    public static final String PARAM_ISBN = "isbn";
    
    /** Parameter name for status. */
    public static final String PARAM_STATUS = "status";
    
    /** Parameter name for order ID. */
    public static final String PARAM_ORDER_ID = "orderId";
    
    /** Parameter name for book ID. */
    public static final String PARAM_BOOK_ID = "bookId";
    
    /** Parameter name for start date. */
    public static final String PARAM_START_DATE = "startDate";
    
    /** Parameter name for end date. */
    public static final String PARAM_END_DATE = "endDate";
    
    /** Parameter name for done flag. */
    public static final String PARAM_DONE = "done";
    
    /** Parameter index for pagination start. */
    public static final int PAGINATION_START = 0;
    
    /** Default page size for pagination. */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * Private constructor to prevent instantiation.
     */
    private JpaConstants() {
        throw new AssertionError("Constants class should not be instantiated");
    }
}
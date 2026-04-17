package bookstore.jdbc;

/**
 * Constants for database operations.
 * Contains table names, column names, and SQL queries.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public final class Constants {
    
    /** Table name for books. */
    public static final String BOOKS_TABLE = "books";
    
    /** Table name for orders. */
    public static final String ORDERS_TABLE = "orders";
    
    /** Table name for order items. */
    public static final String ORDER_ITEMS_TABLE = "order_items";
    
    /** Table name for requests. */
    public static final String REQUESTS_TABLE = "requests";
    
    /** Book ID column. */
    public static final String BOOK_ID = "id";
    
    /** Book ISBN column. */
    public static final String BOOK_ISBN = "isbn";
    
    /** Book title column. */
    public static final String BOOK_TITLE = "title";
    
    /** Book author column. */
    public static final String BOOK_AUTHOR = "author";
    
    /** Book price column. */
    public static final String BOOK_PRICE = "price";
    
    /** Book status column. */
    public static final String BOOK_STATUS = "status";
    
    /** Book publication date column. */
    public static final String BOOK_PUBLICATION_DATE = "publication_date";
    
    /** Book arrival date column. */
    public static final String BOOK_ARRIVAL_DATE = "arrival_date";
    
    /** Book description column. */
    public static final String BOOK_DESCRIPTION = "description";
    
    /** Book created at column. */
    public static final String BOOK_CREATED_AT = "created_at";
    
    /** Book updated at column. */
    public static final String BOOK_UPDATED_AT = "updated_at";
    
    /** Order ID column. */
    public static final String ORDER_ID = "id";
    
    /** Order status column. */
    public static final String ORDER_STATUS = "status";
    
    /** Order creation date column. */
    public static final String ORDER_CREATION_DATE = "creation_date";
    
    /** Order completion date column. */
    public static final String ORDER_COMPLETION_DATE = "completion_date";
    
    /** Order created at column. */
    public static final String ORDER_CREATED_AT = "created_at";
    
    /** Order updated at column. */
    public static final String ORDER_UPDATED_AT = "updated_at";
    
    /** Order item ID column. */
    public static final String ORDER_ITEM_ID = "id";
    
    /** Order item order ID column. */
    public static final String ORDER_ITEM_ORDER_ID = "order_id";
    
    /** Order item book ID column. */
    public static final String ORDER_ITEM_BOOK_ID = "book_id";
    
    /** Order item created at column. */
    public static final String ORDER_ITEM_CREATED_AT = "created_at";
    
    /** Request ID column. */
    public static final String REQUEST_ID = "id";
    
    /** Request order ID column. */
    public static final String REQUEST_ORDER_ID = "order_id";
    
    /** Request book ID column. */
    public static final String REQUEST_BOOK_ID = "book_id";
    
    /** Request done column. */
    public static final String REQUEST_DONE = "done";
    
    /** Request created at column. */
    public static final String REQUEST_CREATED_AT = "created_at";
    
    /** Request updated at column. */
    public static final String REQUEST_UPDATED_AT = "updated_at";
    
    /** SQL to select all books. */
    public static final String SELECT_ALL_BOOKS = 
        "SELECT * FROM " + BOOKS_TABLE + " ORDER BY id";
    
    /** SQL to select book by ID. */
    public static final String SELECT_BOOK_BY_ID = 
        "SELECT * FROM " + BOOKS_TABLE + " WHERE id = ?";
    
    /** SQL to select book by ISBN. */
    public static final String SELECT_BOOK_BY_ISBN = 
        "SELECT * FROM " + BOOKS_TABLE + " WHERE isbn = ?";
    
    /** SQL to insert a book. */
    public static final String INSERT_BOOK = 
        "INSERT INTO " + BOOKS_TABLE + " (isbn, title, author, price, status, "
        + "publication_date, arrival_date, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    /** SQL to update a book. */
    public static final String UPDATE_BOOK = 
        "UPDATE " + BOOKS_TABLE + " SET isbn = ?, title = ?, author = ?, price = ?, "
        + "status = ?, publication_date = ?, arrival_date = ?, description = ? WHERE id = ?";
    
    /** SQL to delete a book. */
    public static final String DELETE_BOOK = 
        "DELETE FROM " + BOOKS_TABLE + " WHERE id = ?";
    
    /** SQL to select books by status. */
    public static final String SELECT_BOOKS_BY_STATUS = 
        "SELECT * FROM " + BOOKS_TABLE + " WHERE status = ? ORDER BY title";
    
    /** SQL to select all orders. */
    public static final String SELECT_ALL_ORDERS = 
        "SELECT * FROM " + ORDERS_TABLE + " ORDER BY creation_date DESC";
    
    /** SQL to select order by ID. */
    public static final String SELECT_ORDER_BY_ID = 
        "SELECT * FROM " + ORDERS_TABLE + " WHERE id = ?";
    
    /** SQL to insert an order. */
    public static final String INSERT_ORDER = 
        "INSERT INTO " + ORDERS_TABLE + " (status, creation_date, completion_date) "
        + "VALUES (?, ?, ?)";
    
    /** SQL to update an order. */
    public static final String UPDATE_ORDER = 
        "UPDATE " + ORDERS_TABLE + " SET status = ?, completion_date = ? WHERE id = ?";
    
    /** SQL to delete an order. */
    public static final String DELETE_ORDER = 
        "DELETE FROM " + ORDERS_TABLE + " WHERE id = ?";
    
    /** SQL to select orders by status. */
    public static final String SELECT_ORDERS_BY_STATUS = 
        "SELECT * FROM " + ORDERS_TABLE + " WHERE status = ? ORDER BY creation_date DESC";
    
    /** SQL to select books by order. */
    public static final String SELECT_BOOKS_BY_ORDER = 
        "SELECT b.* FROM " + BOOKS_TABLE + " b "
        + "INNER JOIN " + ORDER_ITEMS_TABLE + " oi "
        + "ON b.id = oi.book_id WHERE oi.order_id = ?";
    
    /** SQL to delete order items. */
    public static final String DELETE_ORDER_ITEMS = 
        "DELETE FROM " + ORDER_ITEMS_TABLE + " WHERE order_id = ?";
    
    /** SQL to insert an order item. */
    public static final String INSERT_ORDER_ITEM = 
        "INSERT INTO " + ORDER_ITEMS_TABLE + " (order_id, book_id) VALUES (?, ?)";
    
    /** SQL to select all requests. */
    public static final String SELECT_ALL_REQUESTS = 
        "SELECT * FROM " + REQUESTS_TABLE + " ORDER BY created_at DESC";
    
    /** SQL to select request by ID. */
    public static final String SELECT_REQUEST_BY_ID = 
        "SELECT * FROM " + REQUESTS_TABLE + " WHERE id = ?";
    
    /** SQL to insert a request. */
    public static final String INSERT_REQUEST = 
        "INSERT INTO " + REQUESTS_TABLE + " (order_id, book_id, done) VALUES (?, ?, ?)";
    
    /** SQL to update a request. */
    public static final String UPDATE_REQUEST = 
        "UPDATE " + REQUESTS_TABLE + " SET done = ? WHERE id = ?";
    
    /** SQL to delete a request. */
    public static final String DELETE_REQUEST = 
        "DELETE FROM " + REQUESTS_TABLE + " WHERE id = ?";
    
    /** SQL to select requests by order. */
    public static final String SELECT_REQUESTS_BY_ORDER = 
        "SELECT * FROM " + REQUESTS_TABLE + " WHERE order_id = ? ORDER BY created_at";
    
    /** SQL to select requests by book. */
    public static final String SELECT_REQUESTS_BY_BOOK = 
        "SELECT * FROM " + REQUESTS_TABLE + " WHERE book_id = ? ORDER BY created_at";
    
    /** SQL to select active requests. */
    public static final String SELECT_ACTIVE_REQUESTS = 
        "SELECT * FROM " + REQUESTS_TABLE + " WHERE done = false ORDER BY created_at";
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Constants() {
        throw new AssertionError("Константный класс не должен инстанцироваться");
    }
}
package dao;

import di.annotation.Component;
import di.annotation.Inject;
import jdbc.DatabaseConnection;

/**
 * Factory for creating Data Access Objects.
 * Provides centralized access to all DAO instances.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class DAOFactory {
    
    /** Database connection. */
    private final DatabaseConnection connection;
    
    /** Book DAO instance. */
    private BookDAO bookDAO;
    
    /** Order DAO instance. */
    private OrderDAO orderDAO;
    
    /** Request DAO instance. */
    private RequestDAO requestDAO;
    
    /**
     * Constructs a new DAOFactory with the specified connection.
     * 
     * @param connection the database connection
     */
    @Inject
    public DAOFactory(final DatabaseConnection connection) {
        this.connection = connection;
    }
    
    /**
     * Gets or creates a BookDAO instance.
     * 
     * @return the BookDAO instance
     */
    public BookDAO getBookDAO() {
        if (bookDAO == null) {
            bookDAO = new BookDAO(connection);
        }
        return bookDAO;
    }
    
    /**
     * Gets or creates an OrderDAO instance.
     * 
     * @return the OrderDAO instance
     */
    public OrderDAO getOrderDAO() {
        if (orderDAO == null) {
            orderDAO = new OrderDAO(connection, getBookDAO());
        }
        return orderDAO;
    }
    
    /**
     * Gets or creates a RequestDAO instance.
     * 
     * @return the RequestDAO instance
     */
    public RequestDAO getRequestDAO() {
        if (requestDAO == null) {
            requestDAO = new RequestDAO(connection, getBookDAO(), getOrderDAO());
        }
        return requestDAO;
    }
    
    /**
     * Gets the database connection.
     * 
     * @return the database connection
     */
    public DatabaseConnection getConnection() {
        return connection;
    }
}
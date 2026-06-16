package bookstore.dao.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory for creating JPA-based Data Access Objects.
 * Provides centralized access to all JPA DAO instances.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class JpaDAOFactory {
    
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaDAOFactory.class);
    
    /** Book DAO instance. */
    private final JpaBookDAOInterface bookDAO;
    
    /** Order DAO instance. */
    private final JpaOrderDAOInterface orderDAO;
    
    /** Request DAO instance. */
    private final JpaRequestDAOInterface requestDAO;
    
    /**
     * Constructs a new JpaDAOFactory with the specified DAOs.
     * 
     * @param bookDAO the book DAO
     * @param orderDAO the order DAO
     * @param requestDAO the request DAO
     */
    @Autowired
    public JpaDAOFactory(final JpaBookDAOInterface bookDAO,
                         final JpaOrderDAOInterface orderDAO,
                         final JpaRequestDAOInterface requestDAO) {
        this.bookDAO = bookDAO;
        this.orderDAO = orderDAO;
        this.requestDAO = requestDAO;
        LOGGER.info("JpaDAOFactory initialized");
    }
    
    /**
     * Gets the JpaBookDAO instance.
     * 
     * @return the JpaBookDAO instance
     */
    public JpaBookDAOInterface getBookDAO() {
        return bookDAO;
    }
    
    /**
     * Gets the JpaOrderDAO instance.
     * 
     * @return the JpaOrderDAO instance
     */
    public JpaOrderDAOInterface getOrderDAO() {
        return orderDAO;
    }
    
    /**
     * Gets the JpaRequestDAO instance.
     * 
     * @return the JpaRequestDAO instance
     */
    public JpaRequestDAOInterface getRequestDAO() {
        return requestDAO;
    }
}
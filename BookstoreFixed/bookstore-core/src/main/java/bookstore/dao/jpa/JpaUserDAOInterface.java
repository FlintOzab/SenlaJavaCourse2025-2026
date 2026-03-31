package bookstore.dao.jpa;

import bookstore.generics.GenericDAO;
import bookstore.model.User;
import java.util.Optional;

/**
 * DAO interface for User entity operations.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public interface JpaUserDAOInterface extends GenericDAO<User, Integer> {
    
    /**
     * Finds user by username.
     * 
     * @param username the username
     * @return optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Checks if user exists by username.
     * 
     * @param username the username
     * @return true if exists
     */
    boolean existsByUsername(String username);
}
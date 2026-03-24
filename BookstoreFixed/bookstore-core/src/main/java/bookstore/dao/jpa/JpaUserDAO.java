package bookstore.dao.jpa;

import bookstore.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA implementation of User DAO.
 */
@Repository
public class JpaUserDAO implements JpaUserDAOInterface {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaUserDAO.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Optional<User> findById(final Integer id) {
        LOGGER.debug("Finding user by ID: {}", id);
        User user = entityManager.find(User.class, id);
        return Optional.ofNullable(user);
    }
    
    @Override
    public List<User> findAll() {
        LOGGER.debug("Finding all users");
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u ORDER BY u.id", User.class);
        return query.getResultList();
    }
    
    @Override
    @Transactional
    public User save(final User user) {
        LOGGER.debug("Saving user: {}", user.getUsername());
        if (user.getId() == null) {
            entityManager.persist(user);
            LOGGER.info("User saved with ID: {}", user.getId());
            return user;
        } else {
            return entityManager.merge(user);
        }
    }
    
    @Override
    @Transactional
    public User update(final User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("Cannot update user without ID");
        }
        LOGGER.debug("Updating user with ID: {}", user.getId());
        return entityManager.merge(user);
    }
    
    @Override
    @Transactional
    public void delete(final Integer id) {
        LOGGER.debug("Deleting user with ID: {}", id);
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
            LOGGER.info("User deleted with ID: {}", id);
        }
    }
    
    @Override
    public Optional<User> findByUsername(final String username) {
        LOGGER.debug("Finding user by username: {}", username);
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username",
                    User.class);
            query.setParameter("username", username);
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            LOGGER.debug("No user found with username: {}", username);
            return Optional.empty();
        }
    }
    
    @Override
    public boolean existsByUsername(final String username) {
        LOGGER.debug("Checking if user exists by username: {}", username);
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                Long.class);
        query.setParameter("username", username);
        return query.getSingleResult() > 0;
    }
}
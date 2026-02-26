package dao.jpa;

import model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of Request DAO using Hibernate.
 * Provides CRUD operations for Request entities with JPA.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Repository
public class JpaRequestDAO implements JpaRequestDAOInterface {
    
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaRequestDAO.class);
    
    /** Entity manager for JPA operations. */
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Optional<Request> findById(final Integer id) {
        LOGGER.debug("Finding request by ID: {}", id);
        Request request = entityManager.find(Request.class, id);
        return Optional.ofNullable(request);
    }
    
    @Override
    public List<Request> findAll() {
        LOGGER.debug("Finding all requests");
        TypedQuery<Request> query = entityManager.createQuery(
            "SELECT r FROM Request r ORDER BY r.createdAt DESC", Request.class);
        return query.getResultList();
    }
    
    @Override
    public Request save(final Request request) {
        LOGGER.debug("Saving request for order: {}, book: {}", 
            request.getOrder().getId(), request.getBook().getId());
        if (request.getId() == null) {
            entityManager.persist(request);
            LOGGER.info("Request saved with ID: {}", request.getId());
            return request;
        } else {
            return entityManager.merge(request);
        }
    }
    
    @Override
    public Request update(final Request request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Cannot update request without ID");
        }
        LOGGER.debug("Updating request with ID: {}", request.getId());
        return entityManager.merge(request);
    }
    
    @Override
    public void delete(final Integer id) {
        LOGGER.debug("Deleting request with ID: {}", id);
        Request request = entityManager.find(Request.class, id);
        if (request != null) {
            entityManager.remove(request);
            LOGGER.info("Request deleted with ID: {}", id);
        }
    }
    
    @Override
    public List<Request> findByOrderId(final Integer orderId) {
        LOGGER.debug("Finding requests by order ID: {}", orderId);
        TypedQuery<Request> query = entityManager.createQuery(
            "SELECT r FROM Request r WHERE r.order.id = :orderId ORDER BY r.createdAt", 
            Request.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }
    
    @Override
    public List<Request> findByBookId(final Integer bookId) {
        LOGGER.debug("Finding requests by book ID: {}", bookId);
        TypedQuery<Request> query = entityManager.createQuery(
            "SELECT r FROM Request r WHERE r.book.id = :bookId ORDER BY r.createdAt", 
            Request.class);
        query.setParameter("bookId", bookId);
        return query.getResultList();
    }
    
    @Override
    public List<Request> findActiveRequests() {
        LOGGER.debug("Finding active requests");
        TypedQuery<Request> query = entityManager.createQuery(
            "SELECT r FROM Request r WHERE r.done = false ORDER BY r.createdAt", 
            Request.class);
        return query.getResultList();
    }
    
    @Override
    @Transactional
    public int markRequestsAsDoneForOrder(final Integer orderId) {
        LOGGER.debug("Marking requests as done for order: {}", orderId);
        return entityManager.createQuery(
            "UPDATE Request r SET r.done = true WHERE r.order.id = :orderId AND r.done = false")
            .setParameter("orderId", orderId)
            .executeUpdate();
    }
}
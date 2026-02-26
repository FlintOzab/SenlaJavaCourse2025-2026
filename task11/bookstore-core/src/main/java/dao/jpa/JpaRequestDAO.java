package dao.jpa;

import generics.GenericDAO;
import jpa.JpaConstants;
import model.Book;
import model.Order;
import model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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
public class JpaRequestDAO implements GenericDAO<Request, Integer> {
    
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaRequestDAO.class);
    
    /** Entity manager for JPA operations. */
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Default constructor.
     */
    public JpaRequestDAO() {
        // Default constructor for Spring
    }
    
    @Override
    public Optional<Request> findById(final Integer id) {
        LOGGER.debug("Finding request by ID: {}", id);
        Request request = entityManager.find(Request.class, id);
        return Optional.ofNullable(request);
    }
    
    @Override
    public List<Request> findAll() {
        LOGGER.debug("Finding all requests");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Request> cq = cb.createQuery(Request.class);
        Root<Request> root = cq.from(Request.class);
        cq.select(root).orderBy(cb.desc(root.get("createdAt")));
        
        return entityManager.createQuery(cq).getResultList();
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
            Request merged = entityManager.merge(request);
            LOGGER.info("Request updated with ID: {}", merged.getId());
            return merged;
        }
    }
    
    @Override
    public Request update(final Request request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Cannot update request without ID");
        }
        LOGGER.debug("Updating request with ID: {}", request.getId());
        return save(request);
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
    
    /**
     * Finds requests by order ID using named query.
     * 
     * @param orderId the order ID
     * @return list of requests for the order
     */
    public List<Request> findByOrderId(final Integer orderId) {
        LOGGER.debug("Finding requests by order ID: {}", orderId);
        TypedQuery<Request> query = entityManager.createNamedQuery(
            JpaConstants.QUERY_REQUEST_FIND_BY_ORDER_ID, Request.class);
        query.setParameter(JpaConstants.PARAM_ORDER_ID, orderId);
        return query.getResultList();
    }
    
    /**
     * Finds requests by book ID using named query.
     * 
     * @param bookId the book ID
     * @return list of requests for the book
     */
    public List<Request> findByBookId(final Integer bookId) {
        LOGGER.debug("Finding requests by book ID: {}", bookId);
        TypedQuery<Request> query = entityManager.createNamedQuery(
            JpaConstants.QUERY_REQUEST_FIND_BY_BOOK_ID, Request.class);
        query.setParameter(JpaConstants.PARAM_BOOK_ID, bookId);
        return query.getResultList();
    }
    
    /**
     * Finds active (not done) requests using named query.
     * 
     * @return list of active requests
     */
    public List<Request> findActiveRequests() {
        LOGGER.debug("Finding active requests");
        TypedQuery<Request> query = entityManager.createNamedQuery(
            JpaConstants.QUERY_REQUEST_FIND_ACTIVE, Request.class);
        return query.getResultList();
    }
    
    /**
     * Finds requests by order and book.
     * 
     * @param order the order
     * @param book the book
     * @return optional containing the request if found
     */
    public Optional<Request> findByOrderAndBook(final Order order, final Book book) {
        LOGGER.debug("Finding request by order: {} and book: {}", order.getId(), book.getId());
        String jpql = "SELECT r FROM Request r WHERE r.order = :order AND r.book = :book";
        TypedQuery<Request> query = entityManager.createQuery(jpql, Request.class);
        query.setParameter("order", order);
        query.setParameter("book", book);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    /**
     * Counts requests by status.
     * 
     * @param done the status flag
     * @return count of requests with given status
     */
    public long countByStatus(final boolean done) {
        LOGGER.debug("Counting requests by status: done={}", done);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Request> root = cq.from(Request.class);
        
        cq.select(cb.count(root))
          .where(cb.equal(root.get("done"), done));
        
        return entityManager.createQuery(cq).getSingleResult();
    }
    
    /**
     * Marks all requests for an order as done.
     * 
     * @param orderId the order ID
     * @return number of updated requests
     */
    @Transactional
    public int markRequestsAsDoneForOrder(final Integer orderId) {
        LOGGER.debug("Marking requests as done for order: {}", orderId);
        String jpql = "UPDATE Request r SET r.done = true WHERE r.order.id = :orderId AND r.done = false";
        return entityManager.createQuery(jpql)
                .setParameter("orderId", orderId)
                .executeUpdate();
    }
}
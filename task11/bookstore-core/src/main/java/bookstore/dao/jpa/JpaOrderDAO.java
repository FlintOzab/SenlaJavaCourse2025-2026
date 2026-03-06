package bookstore.dao.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import bookstore.model.Order;
import bookstore.model.Order.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of Order DAO using Hibernate.
 * Provides CRUD operations for Order entities with JPA.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Repository
public class JpaOrderDAO implements JpaOrderDAOInterface {
    
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaOrderDAO.class);
    
    /** Entity manager for JPA operations. */
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Optional<Order> findById(final Integer id) {
        LOGGER.debug("Finding order by ID: {}", id);
        Order order = entityManager.find(Order.class, id);
        return Optional.ofNullable(order);
    }
    
    @Override
    public List<Order> findAll() {
        LOGGER.debug("Finding all orders");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> root = cq.from(Order.class);
        cq.select(root).orderBy(cb.desc(root.get("creationDate")));
        
        return entityManager.createQuery(cq).getResultList();
    }
    
    @Override
    public Order save(final Order order) {
        LOGGER.debug("Saving order");
        if (order.getId() == null) {
            entityManager.persist(order);
            LOGGER.info("Order saved with ID: {}", order.getId());
            return order;
        } else {
            return entityManager.merge(order);
        }
    }
    
    @Override
    public Order update(final Order order) {
        if (order.getId() == null) {
            throw new IllegalArgumentException("Cannot update order without ID");
        }
        LOGGER.debug("Updating order with ID: {}", order.getId());
        return entityManager.merge(order);
    }
    
    @Override
    public void delete(final Integer id) {
        LOGGER.debug("Deleting order with ID: {}", id);
        Order order = entityManager.find(Order.class, id);
        if (order != null) {
            entityManager.remove(order);
            LOGGER.info("Order deleted with ID: {}", id);
        }
    }
    
    @Override
    public List<Order> findByStatus(final OrderStatus status) {
        LOGGER.debug("Finding orders by status: {}", status);
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.status = :status ORDER BY o.creationDate DESC", 
            Order.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    @Override
    public List<Order> findCompletedInPeriod(final Date startDate, final Date endDate) {
        LOGGER.debug("Finding completed orders in period: {} - {}", startDate, endDate);
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.status = :status " +
            "AND o.completionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY o.completionDate DESC", 
            Order.class);
        query.setParameter("status", OrderStatus.COMPLETED);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        return query.getResultList();
    }
    
    @Override
    public long getTotalRevenueInPeriod(final Date startDate, final Date endDate) {
        LOGGER.debug("Calculating total revenue for period: {} - {}", startDate, endDate);
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o " +
            "WHERE o.status = :status AND o.completionDate BETWEEN :startDate AND :endDate", 
            Long.class);
        query.setParameter("status", OrderStatus.COMPLETED);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        Long result = query.getSingleResult();
        return result != null ? result : 0L;
    }
    
    @Override
    public long countCompletedInPeriod(final Date startDate, final Date endDate) {
        LOGGER.debug("Counting completed orders in period: {} - {}", startDate, endDate);
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(o) FROM Order o " +
            "WHERE o.status = :status AND o.completionDate BETWEEN :startDate AND :endDate", 
            Long.class);
        query.setParameter("status", OrderStatus.COMPLETED);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        return query.getSingleResult();
    }
}
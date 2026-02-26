package dao.jpa;

import generics.GenericDAO;
import jpa.JpaConstants;
import model.Order;
import model.Order.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

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
public class JpaOrderDAO implements GenericDAO<Order, Integer> {
    
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaOrderDAO.class);
    
    /** Entity manager for JPA operations. */
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Default constructor.
     */
    public JpaOrderDAO() {
        // Default constructor for Spring
    }
    
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
            Order merged = entityManager.merge(order);
            LOGGER.info("Order updated with ID: {}", merged.getId());
            return merged;
        }
    }
    
    @Override
    public Order update(final Order order) {
        if (order.getId() == null) {
            throw new IllegalArgumentException("Cannot update order without ID");
        }
        LOGGER.debug("Updating order with ID: {}", order.getId());
        return save(order);
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
    
    /**
     * Finds orders by status using named query.
     * 
     * @param status the status to search for
     * @return list of orders with the given status
     */
    public List<Order> findByStatus(final OrderStatus status) {
        LOGGER.debug("Finding orders by status: {}", status);
        TypedQuery<Order> query = entityManager.createNamedQuery(
            JpaConstants.QUERY_ORDER_FIND_BY_STATUS, Order.class);
        query.setParameter(JpaConstants.PARAM_STATUS, status);
        return query.getResultList();
    }
    
    /**
     * Finds completed orders in a date period using Criteria API.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of completed orders in the period
     */
    public List<Order> findCompletedInPeriod(final Date startDate, final Date endDate) {
        LOGGER.debug("Finding completed orders in period: {} - {}", startDate, endDate);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> root = cq.from(Order.class);
        
        cq.select(root)
          .where(
              cb.equal(root.get("status"), OrderStatus.COMPLETED),
              cb.between(root.get("completionDate"), startDate, endDate)
          )
          .orderBy(cb.desc(root.get("completionDate")));
        
        return entityManager.createQuery(cq).getResultList();
    }
    
    /**
     * Finds orders created after a specific date.
     * 
     * @param date the cutoff date
     * @return list of orders created after the date
     */
    public List<Order> findCreatedAfter(final Date date) {
        LOGGER.debug("Finding orders created after: {}", date);
        String jpql = "SELECT o FROM Order o WHERE o.creationDate > :date ORDER BY o.creationDate DESC";
        TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
        query.setParameter("date", date);
        return query.getResultList();
    }
    
    /**
     * Finds orders with total price greater than specified amount.
     * 
     * @param minPrice the minimum price
     * @return list of orders meeting the criteria
     */
    public List<Order> findByMinTotalPrice(final long minPrice) {
        LOGGER.debug("Finding orders with min total price: {}", minPrice);
        String jpql = "SELECT o FROM Order o WHERE o.totalPrice >= :minPrice ORDER BY o.totalPrice DESC";
        TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
        query.setParameter("minPrice", minPrice);
        return query.getResultList();
    }
    
    /**
     * Gets total revenue for a period using aggregation.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return total revenue
     */
    public long getTotalRevenueInPeriod(final Date startDate, final Date endDate) {
        LOGGER.debug("Calculating total revenue for period: {} - {}", startDate, endDate);
        String jpql = "SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o " +
                      "WHERE o.status = :status AND o.completionDate BETWEEN :startDate AND :endDate";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("status", OrderStatus.COMPLETED);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getSingleResult();
    }
    
    /**
     * Counts completed orders in a period.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return count of completed orders
     */
    public long countCompletedInPeriod(final Date startDate, final Date endDate) {
        LOGGER.debug("Counting completed orders in period: {} - {}", startDate, endDate);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Order> root = cq.from(Order.class);
        
        cq.select(cb.count(root))
          .where(
              cb.equal(root.get("status"), OrderStatus.COMPLETED),
              cb.between(root.get("completionDate"), startDate, endDate)
          );
        
        return entityManager.createQuery(cq).getSingleResult();
    }
}
package bookstore.dao.jpa;

import java.util.Date;
import java.util.List;

import bookstore.generics.GenericDAO;
import bookstore.model.Order;
import bookstore.model.Order.OrderStatus;

public interface JpaOrderDAOInterface extends GenericDAO<Order, Integer> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findCompletedInPeriod(Date startDate, Date endDate);
    long getTotalRevenueInPeriod(Date startDate, Date endDate);
    long countCompletedInPeriod(Date startDate, Date endDate);
}
package dao.jpa;

import generics.GenericDAO;
import model.Order;
import model.Order.OrderStatus;
import java.util.Date;
import java.util.List;

public interface JpaOrderDAOInterface extends GenericDAO<Order, Integer> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findCompletedInPeriod(Date startDate, Date endDate);
    long getTotalRevenueInPeriod(Date startDate, Date endDate);
    long countCompletedInPeriod(Date startDate, Date endDate);
}
package dao.jpa;

import generics.GenericDAO;
import model.Request;
import java.util.List;

public interface JpaRequestDAOInterface extends GenericDAO<Request, Integer> {
    List<Request> findByOrderId(Integer orderId);
    List<Request> findByBookId(Integer bookId);
    List<Request> findActiveRequests();
    int markRequestsAsDoneForOrder(Integer orderId);
}
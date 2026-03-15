package bookstore.dao.jpa;

import java.util.List;

import bookstore.generics.GenericDAO;
import bookstore.model.Request;

public interface JpaRequestDAOInterface extends GenericDAO<Request, Integer> {
    List<Request> findByOrderId(Integer orderId);
    List<Request> findByBookId(Integer bookId);
    List<Request> findActiveRequests();
    int markRequestsAsDoneForOrder(Integer orderId);
}
package dao.jpa;

import generics.GenericDAO;
import model.Book;
import model.Book.BookStatus;
import java.util.List;
import java.util.Optional;

public interface JpaBookDAOInterface extends GenericDAO<Book, Integer> {
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByStatus(BookStatus status);
    List<Book> findByOrderId(Integer orderId);
    List<Book> findWithPagination(int page, int size);
    long count();
    boolean existsByIsbn(String isbn);
}
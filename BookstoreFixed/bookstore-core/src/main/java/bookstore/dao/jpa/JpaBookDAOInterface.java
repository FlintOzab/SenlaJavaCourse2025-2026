package bookstore.dao.jpa;

import java.util.List;
import java.util.Optional;

import bookstore.generics.GenericDAO;
import bookstore.model.Book;
import bookstore.model.Book.BookStatus;

public interface JpaBookDAOInterface extends GenericDAO<Book, Integer> {
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByStatus(BookStatus status);
    List<Book> findByOrderId(Integer orderId);
    List<Book> findWithPagination(int page, int size);
    long count();
    boolean existsByIsbn(String isbn);
}
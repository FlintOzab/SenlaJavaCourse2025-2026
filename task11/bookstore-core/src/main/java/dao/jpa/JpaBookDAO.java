package dao.jpa;

import model.Book;
import model.Book.BookStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of Book DAO using Hibernate.
 * Provides CRUD operations for Book entities with JPA.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Repository
public class JpaBookDAO implements JpaBookDAOInterface {
    
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaBookDAO.class);
    
    /** Entity manager for JPA operations. */
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Optional<Book> findById(final Integer id) {
        LOGGER.debug("Finding book by ID: {}", id);
        Book book = entityManager.find(Book.class, id);
        return Optional.ofNullable(book);
    }
    
    @Override
    public List<Book> findAll() {
        LOGGER.debug("Finding all books");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> root = cq.from(Book.class);
        cq.select(root).orderBy(cb.asc(root.get("id")));
        
        TypedQuery<Book> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
    
    @Override
    public Book save(final Book book) {
        LOGGER.debug("Saving book: {}", book.getTitle());
        if (book.getId() == null) {
            entityManager.persist(book);
            LOGGER.info("Book saved with ID: {}", book.getId());
            return book;
        } else {
            return entityManager.merge(book);
        }
    }
    
    @Override
    public Book update(final Book book) {
        if (book.getId() == null) {
            throw new IllegalArgumentException("Cannot update book without ID");
        }
        LOGGER.debug("Updating book with ID: {}", book.getId());
        return entityManager.merge(book);
    }
    
    @Override
    public void delete(final Integer id) {
        LOGGER.debug("Deleting book with ID: {}", id);
        Book book = entityManager.find(Book.class, id);
        if (book != null) {
            entityManager.remove(book);
            LOGGER.info("Book deleted with ID: {}", id);
        }
    }
    
    @Override
    public Optional<Book> findByIsbn(final String isbn) {
        LOGGER.debug("Finding book by ISBN: {}", isbn);
        try {
            TypedQuery<Book> query = entityManager.createQuery(
                "SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class);
            query.setParameter("isbn", isbn);
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            LOGGER.debug("No book found with ISBN: {}", isbn);
            return Optional.empty();
        }
    }
    
    @Override
    public List<Book> findByStatus(final BookStatus status) {
        LOGGER.debug("Finding books by status: {}", status);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> root = cq.from(Book.class);
        
        cq.select(root)
          .where(cb.equal(root.get("status"), status))
          .orderBy(cb.asc(root.get("title")));
        
        return entityManager.createQuery(cq).getResultList();
    }
    
    @Override
    public List<Book> findByOrderId(final Integer orderId) {
        LOGGER.debug("Finding books by order ID: {}", orderId);
        TypedQuery<Book> query = entityManager.createQuery(
            "SELECT b FROM Order o JOIN o.books b WHERE o.id = :orderId", Book.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }
    
    @Override
    public List<Book> findWithPagination(final int page, final int size) {
        LOGGER.debug("Finding books with pagination - page: {}, size: {}", page, size);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> root = cq.from(Book.class);
        cq.select(root).orderBy(cb.asc(root.get("id")));
        
        TypedQuery<Book> query = entityManager.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        
        return query.getResultList();
    }
    
    @Override
    public long count() {
        LOGGER.debug("Counting all books");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(Book.class)));
        return entityManager.createQuery(cq).getSingleResult();
    }
    
    @Override
    public boolean existsByIsbn(final String isbn) {
        LOGGER.debug("Checking if book exists by ISBN: {}", isbn);
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM Book b WHERE b.isbn = :isbn", Long.class);
        query.setParameter("isbn", isbn);
        return query.getSingleResult() > 0;
    }
}
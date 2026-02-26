package dao.jpa;

import generics.GenericDAO;
import jpa.JpaConstants;
import model.Book;
import model.Book.BookStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
public class JpaBookDAO implements GenericDAO<Book, Integer> {
    
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaBookDAO.class);
    
    /** Entity manager for JPA operations. */
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Default constructor.
     */
    public JpaBookDAO() {
        // Default constructor for Spring
    }
    
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
            Book merged = entityManager.merge(book);
            LOGGER.info("Book updated with ID: {}", merged.getId());
            return merged;
        }
    }
    
    @Override
    public Book update(final Book book) {
        if (book.getId() == null) {
            throw new IllegalArgumentException("Cannot update book without ID");
        }
        LOGGER.debug("Updating book with ID: {}", book.getId());
        return save(book);
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
    
    /**
     * Finds a book by its ISBN using named query.
     * 
     * @param isbn the ISBN to search for
     * @return optional containing the book if found
     */
    public Optional<Book> findByIsbn(final String isbn) {
        LOGGER.debug("Finding book by ISBN: {}", isbn);
        try {
            TypedQuery<Book> query = entityManager.createNamedQuery(
                JpaConstants.QUERY_BOOK_FIND_BY_ISBN, Book.class);
            query.setParameter(JpaConstants.PARAM_ISBN, isbn);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            LOGGER.debug("No book found with ISBN: {}", isbn);
            return Optional.empty();
        }
    }
    
    /**
     * Finds books by their status using Criteria API.
     * 
     * @param status the status to search for
     * @return list of books with the given status
     */
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
    
    /**
     * Finds books by order ID using JPQL.
     * 
     * @param orderId the order ID
     * @return list of books in the order
     */
    public List<Book> findByOrderId(final Integer orderId) {
        LOGGER.debug("Finding books by order ID: {}", orderId);
        TypedQuery<Book> query = entityManager.createNamedQuery(
            JpaConstants.QUERY_BOOK_FIND_BY_ORDER_ID, Book.class);
        query.setParameter(JpaConstants.PARAM_ORDER_ID, orderId);
        return query.getResultList();
    }
    
    /**
     * Finds books with pagination using Criteria API.
     * 
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of books for the page
     */
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
    
    /**
     * Counts total number of books.
     * 
     * @return total book count
     */
    public long count() {
        LOGGER.debug("Counting all books");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(Book.class)));
        return entityManager.createQuery(cq).getSingleResult();
    }
    
    /**
     * Checks if a book exists by ISBN.
     * 
     * @param isbn the ISBN to check
     * @return true if exists
     */
    public boolean existsByIsbn(final String isbn) {
        LOGGER.debug("Checking if book exists by ISBN: {}", isbn);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Book> root = cq.from(Book.class);
        
        cq.select(cb.count(root))
          .where(cb.equal(root.get("isbn"), isbn));
        
        Long count = entityManager.createQuery(cq).getSingleResult();
        return count > 0;
    }
}
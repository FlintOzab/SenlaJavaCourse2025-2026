package dao;

import generics.GenericDAO;
import jdbc.Constants;
import jdbc.DatabaseConnection;
import jdbc.DatabaseException;
import model.Book;
import model.Book.BookStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Book entities.
 * Provides CRUD operations for books in the database.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class BookDAO implements GenericDAO<Book, Integer> {
    
    /** Database connection. */
    private final DatabaseConnection connection;
    
    /**
     * Constructs a new BookDAO with the specified connection.
     * 
     * @param connection the database connection
     */
    public BookDAO(final DatabaseConnection connection) {
        this.connection = connection;
    }
    
    @Override
    public Optional<Book> findById(final Integer id) {
        String sql = Constants.SELECT_BOOK_BY_ID;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(DAOConstants.PARAM_INDEX_1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_BOOK_NOT_FOUND_BY_ID + id, e);
        }
    }
    
    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = Constants.SELECT_ALL_BOOKS;
        
        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
            return books;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_BOOK_FETCH_FAILED, e);
        }
    }
    
    @Override
    public Book save(final Book book) {
        String sql = Constants.INSERT_BOOK;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setBookParameters(stmt, book);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException(DAOConstants.ERROR_BOOK_SAVE_FAILED + book);
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(DAOConstants.GENERATED_KEYS_INDEX));
                } else {
                    throw new DatabaseException(DAOConstants.ERROR_NO_GENERATED_ID);
                }
            }
            return book;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_BOOK_SAVE_FAILED + book, e);
        }
    }
    
    @Override
    public Book update(final Book book) {
        if (book.getId() == null) {
            throw new DatabaseException(DAOConstants.ERROR_BOOK_UPDATE_NO_ID);
        }
        
        String sql = Constants.UPDATE_BOOK;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setBookParameters(stmt, book);
            stmt.setInt(DAOConstants.BOOK_UPDATE_ID_INDEX, book.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException(DAOConstants.ERROR_BOOK_UPDATE_FAILED + book.getId());
            }
            return book;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_BOOK_UPDATE_FAILED + book.getId(), e);
        }
    }
    
    @Override
    public void delete(final Integer id) {
        String sql = Constants.DELETE_BOOK;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(DAOConstants.PARAM_INDEX_1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException(DAOConstants.ERROR_BOOK_DELETE_FAILED + id);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_BOOK_DELETE_FAILED + id, e);
        }
    }
    
    /**
     * Deletes a book by its entity.
     * 
     * @param book the book to delete
     */
    public void deleteByBook(final Book book) {
        if (book.getId() != null) {
            delete(book.getId());
        }
    }
    
    /**
     * Finds a book by its ISBN.
     * 
     * @param isbn the ISBN to search for
     * @return optional containing the book if found
     */
    public Optional<Book> findByIsbn(final String isbn) {
        String sql = Constants.SELECT_BOOK_BY_ISBN;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(DAOConstants.PARAM_INDEX_1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_BOOK_NOT_FOUND_BY_ISBN + isbn, e);
        }
    }
    
    /**
     * Finds books by their status.
     * 
     * @param status the status to search for
     * @return list of books with the given status
     */
    public List<Book> findByStatus(final BookStatus status) {
        List<Book> books = new ArrayList<>();
        String sql = Constants.SELECT_BOOKS_BY_STATUS;
        
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(DAOConstants.PARAM_INDEX_1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
            return books;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_BOOKS_NOT_FOUND_BY_STATUS + status, e);
        }
    }
    
    /**
     * Maps a ResultSet row to a Book object.
     * 
     * @param rs the ResultSet
     * @return the mapped Book
     * @throws SQLException if mapping fails
     */
    public Book mapResultSetToBook(final ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt(Constants.BOOK_ID));
        book.setIsbn(rs.getString(Constants.BOOK_ISBN));
        book.setTitle(rs.getString(Constants.BOOK_TITLE));
        book.setAuthor(rs.getString(Constants.BOOK_AUTHOR));
        book.setPrice(rs.getLong(Constants.BOOK_PRICE));
        book.setStatus(BookStatus.valueOf(rs.getString(Constants.BOOK_STATUS)));
        book.setPublicationDate(rs.getTimestamp(Constants.BOOK_PUBLICATION_DATE));
        book.setArrivalDate(rs.getTimestamp(Constants.BOOK_ARRIVAL_DATE));
        book.setDescription(rs.getString(Constants.BOOK_DESCRIPTION));
        return book;
    }
    
    /**
     * Sets prepared statement parameters for a book.
     * 
     * @param stmt the prepared statement
     * @param book the book
     * @throws SQLException if parameter setting fails
     */
    private void setBookParameters(final PreparedStatement stmt, 
                                    final Book book) throws SQLException {
        stmt.setString(DAOConstants.PARAM_INDEX_1, book.getIsbn());
        stmt.setString(DAOConstants.PARAM_INDEX_2, book.getTitle());
        stmt.setString(DAOConstants.PARAM_INDEX_3, book.getAuthor());
        stmt.setLong(DAOConstants.PARAM_INDEX_4, book.getPrice());
        stmt.setString(DAOConstants.PARAM_INDEX_5, book.getStatus().name());
        stmt.setTimestamp(DAOConstants.PARAM_INDEX_6, new Timestamp(book.getPublicationDate().getTime()));
        stmt.setTimestamp(DAOConstants.PARAM_INDEX_7, new Timestamp(book.getArrivalDate().getTime()));
        stmt.setString(DAOConstants.PARAM_INDEX_8, book.getDescription());
    }
}
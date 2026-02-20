package dao;

import generics.GenericDAO;
import jdbc.Constants;
import jdbc.DatabaseConnection;
import jdbc.DatabaseException;
import model.Book;
import model.Order;
import model.Request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Request entities.
 * Provides CRUD operations for requests in the database.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class RequestDAO implements GenericDAO<Request, Integer> {
    
    /** Database connection. */
    private final DatabaseConnection connection;
    
    /** Book DAO for loading associated books. */
    private final BookDAO bookDAO;
    
    /** Order DAO for loading associated orders. */
    private final OrderDAO orderDAO;
    
    /**
     * Constructs a new RequestDAO with the specified dependencies.
     * 
     * @param connection the database connection
     * @param bookDAO the book DAO
     * @param orderDAO the order DAO
     */
    public RequestDAO(final DatabaseConnection connection,
                       final BookDAO bookDAO,
                       final OrderDAO orderDAO) {
        this.connection = connection;
        this.bookDAO = bookDAO;
        this.orderDAO = orderDAO;
    }
    
    @Override
    public Optional<Request> findById(final Integer id) {
        String sql = Constants.SELECT_REQUEST_BY_ID;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(DAOConstants.PARAM_INDEX_1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRequest(rs));
                }
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_REQUEST_NOT_FOUND_BY_ID + id, e);
        }
    }
    
    @Override
    public List<Request> findAll() {
        List<Request> requests = new ArrayList<>();
        String sql = Constants.SELECT_ALL_REQUESTS;
        
        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
            return requests;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_REQUEST_FETCH_FAILED, e);
        }
    }
    
    @Override
    public Request save(final Request request) {
        String sql = Constants.INSERT_REQUEST;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(DAOConstants.PARAM_INDEX_1, request.getOrder().getId());
            stmt.setInt(DAOConstants.PARAM_INDEX_2, request.getBook().getId());
            stmt.setBoolean(DAOConstants.PARAM_INDEX_3, request.isDone());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException(DAOConstants.ERROR_REQUEST_SAVE_FAILED + request);
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    request.setId(generatedKeys.getInt(DAOConstants.GENERATED_KEYS_INDEX));
                } else {
                    throw new DatabaseException(DAOConstants.ERROR_NO_GENERATED_ID);
                }
            }
            return request;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_REQUEST_SAVE_FAILED + request, e);
        }
    }
    
    @Override
    public Request update(final Request request) {
        if (request.getId() == null) {
            throw new DatabaseException(DAOConstants.ERROR_REQUEST_UPDATE_NO_ID);
        }
        
        String sql = Constants.UPDATE_REQUEST;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(DAOConstants.PARAM_INDEX_1, request.isDone());
            stmt.setInt(DAOConstants.PARAM_INDEX_2, request.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException(DAOConstants.ERROR_REQUEST_UPDATE_FAILED + request.getId());
            }
            return request;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_REQUEST_UPDATE_FAILED + request.getId(), e);
        }
    }
    
    @Override
    public void delete(final Integer id) {
        String sql = Constants.DELETE_REQUEST;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(DAOConstants.PARAM_INDEX_1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException(DAOConstants.ERROR_REQUEST_DELETE_FAILED + id);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_REQUEST_DELETE_FAILED + id, e);
        }
    }
    
    /**
     * Deletes a request by its entity.
     * 
     * @param request the request to delete
     */
    public void deleteByRequest(final Request request) {
        if (request.getId() != null) {
            delete(request.getId());
        }
    }
    
    /**
     * Finds requests by order ID.
     * 
     * @param orderId the order ID
     * @return list of requests for the order
     */
    public List<Request> findByOrderId(final Integer orderId) {
        List<Request> requests = new ArrayList<>();
        String sql = Constants.SELECT_REQUESTS_BY_ORDER;
        
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(DAOConstants.PARAM_INDEX_1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToRequest(rs));
                }
            }
            return requests;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_REQUESTS_NOT_FOUND_BY_ORDER + orderId, e);
        }
    }
    
    /**
     * Finds requests by book ID.
     * 
     * @param bookId the book ID
     * @return list of requests for the book
     */
    public List<Request> findByBookId(final Integer bookId) {
        List<Request> requests = new ArrayList<>();
        String sql = Constants.SELECT_REQUESTS_BY_BOOK;
        
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(DAOConstants.PARAM_INDEX_1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToRequest(rs));
                }
            }
            return requests;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_REQUESTS_NOT_FOUND_BY_BOOK + bookId, e);
        }
    }
    
    /**
     * Finds all active (not done) requests.
     * 
     * @return list of active requests
     */
    public List<Request> findActiveRequests() {
        List<Request> requests = new ArrayList<>();
        String sql = Constants.SELECT_ACTIVE_REQUESTS;
        
        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
            return requests;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_ACTIVE_REQUESTS_FAILED, e);
        }
    }
    
    /**
     * Maps a ResultSet row to a Request object.
     * 
     * @param rs the ResultSet
     * @return the mapped Request
     * @throws SQLException if mapping fails
     */
    private Request mapResultSetToRequest(final ResultSet rs) throws SQLException {
        Request request = new Request();
        request.setId(rs.getInt(Constants.REQUEST_ID));
        request.setDone(rs.getBoolean(Constants.REQUEST_DONE));
        
        Integer orderId = rs.getInt(Constants.REQUEST_ORDER_ID);
        Integer bookId = rs.getInt(Constants.REQUEST_BOOK_ID);
        
        Order order = orderDAO.findById(orderId)
            .orElseThrow(() -> new DatabaseException(DAOConstants.ERROR_ORDER_NOT_FOUND + orderId));
        Book book = bookDAO.findById(bookId)
            .orElseThrow(() -> new DatabaseException(DAOConstants.ERROR_BOOK_NOT_FOUND + bookId));
        
        request.setOrder(order);
        request.setBook(book);
        
        return request;
    }
}
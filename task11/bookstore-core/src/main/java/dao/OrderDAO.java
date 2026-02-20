package dao;

import generics.GenericDAO;
import jdbc.Constants;
import jdbc.DatabaseConnection;
import jdbc.DatabaseException;
import model.Book;
import model.Order;
import model.Order.OrderStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Order entities.
 * Provides CRUD operations for orders in the database.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class OrderDAO implements GenericDAO<Order, Integer> {
    
    /** Database connection. */
    private final DatabaseConnection connection;
    
    /** Book DAO for loading associated books. */
    private final BookDAO bookDAO;
    
    /**
     * Constructs a new OrderDAO with the specified dependencies.
     * 
     * @param connection the database connection
     * @param bookDAO the book DAO
     */
    public OrderDAO(final DatabaseConnection connection, final BookDAO bookDAO) {
        this.connection = connection;
        this.bookDAO = bookDAO;
    }
    
    @Override
    public Optional<Order> findById(final Integer id) {
        String sql = Constants.SELECT_ORDER_BY_ID;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(DAOConstants.PARAM_INDEX_1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    loadOrderBooks(order);
                    return Optional.of(order);
                }
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_ORDER_NOT_FOUND_BY_ID + id, e);
        }
    }
    
    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = Constants.SELECT_ALL_ORDERS;
        
        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                loadOrderBooks(order);
                orders.add(order);
            }
            return orders;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_ORDER_FETCH_FAILED, e);
        }
    }
    
    @Override
    public Order save(final Order order) {
        String sql = Constants.INSERT_ORDER;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(DAOConstants.PARAM_INDEX_1, order.getStatus().name());
            stmt.setTimestamp(DAOConstants.PARAM_INDEX_2, new Timestamp(order.getCreationDate().getTime()));
            
            if (order.getCompletionDate() != null) {
                stmt.setTimestamp(DAOConstants.PARAM_INDEX_3, new Timestamp(order.getCompletionDate().getTime()));
            } else {
                stmt.setNull(DAOConstants.PARAM_INDEX_3, Types.TIMESTAMP);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException(DAOConstants.ERROR_ORDER_SAVE_FAILED);
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getInt(DAOConstants.GENERATED_KEYS_INDEX));
                } else {
                    throw new DatabaseException(DAOConstants.ERROR_NO_GENERATED_ID);
                }
            }
            
            saveOrderBooks(order);
            return order;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_ORDER_SAVE_FAILED, e);
        }
    }
    
    @Override
    public Order update(final Order order) {
        if (order.getId() == null) {
            throw new DatabaseException(DAOConstants.ERROR_ORDER_UPDATE_NO_ID);
        }
        
        String sql = Constants.UPDATE_ORDER;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(DAOConstants.ORDER_UPDATE_STATUS_INDEX, order.getStatus().name());
            
            if (order.getCompletionDate() != null) {
                stmt.setTimestamp(DAOConstants.ORDER_UPDATE_COMPLETION_INDEX, 
                    new Timestamp(order.getCompletionDate().getTime()));
            } else {
                stmt.setNull(DAOConstants.ORDER_UPDATE_COMPLETION_INDEX, Types.TIMESTAMP);
            }
            
            stmt.setInt(DAOConstants.ORDER_UPDATE_ID_INDEX, order.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException(DAOConstants.ERROR_ORDER_UPDATE_FAILED + order.getId());
            }
            
            updateOrderBooks(order);
            return order;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_ORDER_UPDATE_FAILED + order.getId(), e);
        }
    }
    
    @Override
    public void delete(final Integer id) {
        String sql = Constants.DELETE_ORDER;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(DAOConstants.ORDER_DELETE_ID_INDEX, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException(DAOConstants.ERROR_ORDER_DELETE_FAILED + id);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_ORDER_DELETE_FAILED + id, e);
        }
    }
    
    /**
     * Deletes an order by its entity.
     * 
     * @param order the order to delete
     */
    public void deleteByOrder(final Order order) {
        if (order.getId() != null) {
            delete(order.getId());
        }
    }
    
    /**
     * Finds orders by their status.
     * 
     * @param status the status to search for
     * @return list of orders with the given status
     */
    public List<Order> findByStatus(final OrderStatus status) {
        List<Order> orders = new ArrayList<>();
        String sql = Constants.SELECT_ORDERS_BY_STATUS;
        
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(DAOConstants.PARAM_INDEX_1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    loadOrderBooks(order);
                    orders.add(order);
                }
            }
            return orders;
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_ORDERS_NOT_FOUND_BY_STATUS + status, e);
        }
    }
    
    /**
     * Maps a ResultSet row to an Order object.
     * 
     * @param rs the ResultSet
     * @return the mapped Order
     * @throws SQLException if mapping fails
     */
    private Order mapResultSetToOrder(final ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt(Constants.ORDER_ID));
        order.setStatus(OrderStatus.valueOf(rs.getString(Constants.ORDER_STATUS)));
        
        Timestamp creationDate = rs.getTimestamp(Constants.ORDER_CREATION_DATE);
        if (creationDate != null) {
            order.setCreationDate(new Date(creationDate.getTime()));
        }
        
        Timestamp completionDate = rs.getTimestamp(Constants.ORDER_COMPLETION_DATE);
        if (completionDate != null) {
            order.setCompletionDate(new Date(completionDate.getTime()));
        }
        
        return order;
    }
    
    /**
     * Loads books associated with an order.
     * 
     * @param order the order
     */
    private void loadOrderBooks(final Order order) {
        String sql = Constants.SELECT_BOOKS_BY_ORDER;
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(DAOConstants.PARAM_INDEX_1, order.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                List<Book> books = new ArrayList<>();
                while (rs.next()) {
                    books.add(bookDAO.mapResultSetToBook(rs));
                }
                order.setBooks(books);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_ORDER_BOOKS_LOAD_FAILED + order.getId(), e);
        }
    }
    
    /**
     * Saves books associated with an order.
     * 
     * @param order the order
     */
    private void saveOrderBooks(final Order order) {
        String deleteSql = Constants.DELETE_ORDER_ITEMS;
        String insertSql = Constants.INSERT_ORDER_ITEM;
        
        try (Connection conn = connection.getConnection()) {
            // Delete existing associations
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(DAOConstants.PARAM_INDEX_1, order.getId());
                deleteStmt.executeUpdate();
            }
            
            // Insert new associations
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Book book : order.getBooks()) {
                    insertStmt.setInt(DAOConstants.PARAM_INDEX_1, order.getId());
                    insertStmt.setInt(DAOConstants.PARAM_INDEX_2, book.getId());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException(DAOConstants.ERROR_ORDER_BOOKS_SAVE_FAILED + order.getId(), e);
        }
    }
    
    /**
     * Updates books associated with an order.
     * 
     * @param order the order
     */
    private void updateOrderBooks(final Order order) {
        saveOrderBooks(order);
    }
}
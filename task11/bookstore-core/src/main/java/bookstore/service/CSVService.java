package bookstore.service;

import org.springframework.stereotype.Component;

import bookstore.exception.BookstoreException;
import bookstore.exception.CSVImportException;
import bookstore.model.Book;
import bookstore.model.Order;
import bookstore.model.Request;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for CSV import/export operations.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class CSVService {
    
    /** Header for books CSV. */
    private static final String BOOKS_HEADER = 
        "ID,ISBN,Title,Author,Price,Status,PublicationDate,ArrivalDate,Description";
    
    /** Header for orders CSV. */
    private static final String ORDERS_HEADER = 
        "OrderID,BookIDs,Status,CreationDate,CompletionDate";
    
    /** Header for requests CSV. */
    private static final String REQUESTS_HEADER = 
        "ID,RequestID,OrderID,BookID,Done";
    
    /**
     * Exports books to CSV file.
     * 
     * @param books the list of books
     * @param filePath the file path
     * @throws BookstoreException if export fails
     */
    public void exportBooksToCSV(final List<Book> books, 
                                  final String filePath) throws BookstoreException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(BOOKS_HEADER);
            for (Book book : books) {
                writer.println(book.toCSV());
            }
        } catch (IOException e) {
            throw new BookstoreException(
                "Ошибка экспорта книг в CSV: " + e.getMessage(), e);
        }
    }
    
    /**
     * Imports books from CSV file.
     * 
     * @param filePath the file path
     * @return list of imported books
     * @throws BookstoreException if import fails
     */
    public List<Book> importBooksFromCSV(final String filePath) throws BookstoreException {
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Skip header
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Book book = Book.fromCSV(line);
                    books.add(book);
                } catch (CSVImportException e) {
                    System.err.println("Пропуск строки из-за ошибки: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new BookstoreException(
                "Ошибка импорта книг из CSV: " + e.getMessage(), e);
        }
        return books;
    }
    
    /**
     * Exports orders to CSV file.
     * 
     * @param orders the list of orders
     * @param filePath the file path
     * @throws BookstoreException if export fails
     */
    public void exportOrdersToCSV(final List<Order> orders, 
                                   final String filePath) throws BookstoreException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(ORDERS_HEADER);
            for (Order order : orders) {
                writer.println(order.toCSV());
            }
        } catch (IOException e) {
            throw new BookstoreException(
                "Ошибка экспорта заказов в CSV: " + e.getMessage(), e);
        }
    }
    
    /**
     * Imports orders from CSV file.
     * 
     * @param filePath the file path
     * @return list of imported orders
     * @throws BookstoreException if import fails
     */
    public List<Order> importOrdersFromCSV(final String filePath) throws BookstoreException {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Skip header
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Order order = Order.fromCSV(line);
                    orders.add(order);
                } catch (CSVImportException e) {
                    System.err.println(
                        "Пропуск строки заказа из-за ошибки: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new BookstoreException(
                "Ошибка импорта заказов из CSV: " + e.getMessage(), e);
        }
        return orders;
    }
    
    /**
     * Exports requests to CSV file.
     * 
     * @param requests the list of requests
     * @param filePath the file path
     * @throws BookstoreException if export fails
     */
    public void exportRequestsToCSV(final List<Request> requests,
                                     final String filePath) throws BookstoreException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(REQUESTS_HEADER);
            for (Request request : requests) {
                writer.println(request.toCSV());
            }
        } catch (IOException e) {
            throw new BookstoreException(
                "Ошибка экспорта запросов в CSV: " + e.getMessage(), e);
        }
    }
}
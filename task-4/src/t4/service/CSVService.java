package t4.service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import di.annotation.Component;
import t4.exception.BookstoreException;
import t4.exception.CSVImportException;
import t4.model.Book;
import t4.model.Order;
import t4.model.Request;

@Component
public class CSVService {
	
	public void exportBooksToCSV(List<Book> books, String filePath) throws BookstoreException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("ID,ISBN,Title,Author,Price,Status,PublicationDate,ArrivalDate,Description");
            for (Book book : books) {
                writer.println(book.toCSV());
            }
        } catch (IOException e) {
            throw new BookstoreException("Ошибка экспорта книг в CSV: " + e.getMessage(), e);
        }
    }
    
    public List<Book> importBooksFromCSV(String filePath) throws BookstoreException {
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                try {
                    Book book = Book.fromCSV(line);
                    books.add(book);
                } catch (CSVImportException e) {
                    System.err.println("Пропуск строки из-за ошибки: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new BookstoreException("Ошибка импорта книг из CSV: " + e.getMessage(), e);
        }
        return books;
    }
    
    public void exportOrdersToCSV(List<Order> orders, String filePath) throws BookstoreException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("OrderID,BookIDs,Status,CreationDate,CompletionDate");
            for (Order order : orders) {
                writer.println(order.toCSV());
            }
        } catch (IOException e) {
            throw new BookstoreException("Ошибка экспорта заказов в CSV: " + e.getMessage(), e);
        }
    }
    
    public List<Order> importOrdersFromCSV(String filePath) throws BookstoreException {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                try {
                    Order order = Order.fromCSV(line);
                    orders.add(order);
                } catch (CSVImportException e) {
                    System.err.println("Пропуск строки заказа из-за ошибки: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new BookstoreException("Ошибка импорта заказов из CSV: " + e.getMessage(), e);
        }
        return orders;
    }
    
    public void exportRequestsToCSV(List<Request> requests, String filePath) throws BookstoreException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("ID,RequestID,OrderID,BookID,Done");
            for (Request request : requests) {
                writer.println(request.toCSV());
            }
        } catch (IOException e) {
            throw new BookstoreException("Ошибка экспорта запросов в CSV: " + e.getMessage(), e);
        }
    }
}

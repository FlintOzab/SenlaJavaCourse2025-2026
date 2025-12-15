package t4;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import t4.Book.BookStatus;


public class Bookstore {
	private List<Book> bookInventory;
    private List<Request> existingRequests;
    private List<Order> orders;
    private final CSVService csvService;
    
    public Bookstore() {
        this.bookInventory = new ArrayList<>();
        this.existingRequests = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.csvService = new CSVService();
    }

    public void addBookToInventory(Book book) throws ValidationException {
        validateBook(book);
        bookInventory.add(book);
        book.setStatus(BookStatus.IN_STOCK);
        fulfillBookRequests(book);
    }
    
    private void validateBook(Book book) throws ValidationException {
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new ValidationException("ISBN книги не может быть пустым");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new ValidationException("Название книги не может быть пустым");
        }
        if (book.getPrice() == null || book.getPrice() < 0) {
            throw new ValidationException("Цена книги должна быть неотрицательной");
        }
    }
    
    public void writeOffBook(String isbn) throws EntityNotFoundException {
        Book book = findBookByIsbn(isbn);
        if (book == null) {
            throw new EntityNotFoundException("Книга с ISBN " + isbn + " не найдена");
        }
        book.setStatus(BookStatus.OUT_OF_STOCK);
    }
    
    public Order createOrder(int orderId, List<Integer> bookIds) throws ValidationException {
        if (bookIds == null || bookIds.isEmpty()) {
            throw new ValidationException("Заказ должен содержать хотя бы одну книгу");
        }
        if (findOrderById(orderId) != null) {
            throw new ValidationException("Заказ с номером " + orderId + " уже существует");
        }
        
        List<Book> selectedBooks = new ArrayList<>();
        for (int bookId : bookIds) {
            Book book = findBookById(bookId);
            if (book == null) {
                throw new ValidationException("Книга с ID " + bookId + " не найдена");
            }
            selectedBooks.add(book);
        }
        
        Order order = new Order(orderId, selectedBooks);
        orders.add(order);
        getRequestsOnBooksOutOfStock(order);
        return order;
    }
    
    public void completeOrder(int orderId) throws EntityNotFoundException, ValidationException {
        Order order = findOrderById(orderId);
        if (order == null) {
            throw new EntityNotFoundException("Заказ с номером " + orderId + " не найден");
        }
        if (order.getStatus() != Order.OrderStatus.NEW) {
            throw new ValidationException("Можно завершать только новые заказы");
        }
        if (order.containsOutOfStockBooks()) {
            throw new ValidationException("Невозможно завершить заказ с отсутствующими книгами");
        }
        
        order.completeOrder();
    }

    public void cancelOrder(int orderId) throws EntityNotFoundException, ValidationException {
        Order order = findOrderById(orderId);
        if (order == null) {
            throw new EntityNotFoundException("Заказ с ID " + orderId + " не найден");
        }
        if (order.getStatus() != Order.OrderStatus.NEW) {
            throw new ValidationException("Можно отменять только новые заказы");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
    }
    
    public void updateOrderStatus(int orderId, Order.OrderStatus newStatus) 
            throws EntityNotFoundException, ValidationException {
        Order order = findOrderById(orderId);
        if (order == null) {
            throw new EntityNotFoundException("Заказ с ID " + orderId + " не найден");
        }
        
        if (newStatus == Order.OrderStatus.COMPLETED && order.containsOutOfStockBooks()) {
            throw new ValidationException("Невозможно завершить заказ с отсутствующими книгами");
        }
        
        order.setStatus(newStatus);
        if (newStatus == Order.OrderStatus.COMPLETED) {
            order.completeOrder();
        }
    }
    
    public void createBookRequest(Order order, Book book) {
        Request request = new Request(order, book);
        existingRequests.add(request);
    }
    
    public Order findOrderById(int orderId) {
        return orders.stream()
            .filter(order -> order.getOrderId() == orderId)
            .findFirst()
            .orElse(null);
    }
    
    public Book findBookByIsbn(String isbn) {
        return bookInventory.stream()
            .filter(book -> book.getIsbn().equals(isbn))
            .findFirst()
            .orElse(null);
    }
    
    public Book findBookById(int id) {
        return bookInventory.stream()
            .filter(book -> book.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    private void getRequestsOnBooksOutOfStock(Order order) {
        for (Book book : order.getOutOfStockBooks()) {
            if (!existingRequests.stream().anyMatch(request -> request.matchesBook(book))) {
                existingRequests.add(new Request(order, book));
            }
        }
    }
    
    public void fulfillBookRequests(Book book) {
        for (Request request : existingRequests) {
            if (!request.isDone() && request.matchesBook(book)) {
                request.setDone(true);
            }
        }
    }
   
    public void importBooksFromCSV(String filePath) throws BookstoreException {
        List<Book> importedBooks = csvService.importBooksFromCSV(filePath);
        for (Book importedBook : importedBooks) {
            Book existingBook = findBookById(importedBook.getId());
            if (existingBook != null) {
                updateBookFromImport(existingBook, importedBook);
            } else {
                bookInventory.add(importedBook);
                fulfillBookRequests(importedBook);
            }
        }
    }
    
    private void updateBookFromImport(Book existing, Book imported) {
        existing.setIsbn(imported.getIsbn());
        existing.setTitle(imported.getTitle());
        existing.setAuthor(imported.getAuthor());
        existing.setPrice(imported.getPrice());
        existing.setStatus(imported.getStatus());
        existing.setPublicationDate(imported.getPublicationDate());
        existing.setArrivalDate(imported.getArrivalDate());
        existing.setDescription(imported.getDescription());
    }
    
    public void importOrdersFromCSV(String filePath) throws BookstoreException {
        csvService.importOrdersFromCSV(filePath);
    }
    
    public void exportBooksToCSV(String filePath) throws BookstoreException {
        csvService.exportBooksToCSV(bookInventory, filePath);
    }
    
    public void exportOrdersToCSV(String filePath) throws BookstoreException {
        csvService.exportOrdersToCSV(orders, filePath);
    }
    
    public void exportRequestsToCSV(String filePath) throws BookstoreException {
        csvService.exportRequestsToCSV(existingRequests, filePath);
    }
    
    public List<Book> getBookInventory() { return new ArrayList<>(bookInventory); }
    public List<Request> getExistingRequests() { return new ArrayList<>(existingRequests); }
    public List<Order> getAllOrders() { return new ArrayList<>(orders); }
    
    public List<Request> getActiveRequests() {
        return existingRequests.stream()
            .filter(request -> !request.isDone())
            .collect(Collectors.toList());
    }
   
    public List<Book> getBooksSortedByTitle() {
        return bookInventory.stream()
            .sorted(Comparator.comparing(Book::getTitle))
            .collect(Collectors.toList());
    }
    
    public List<Book> getBooksSortedByPublicationDate() {
        return bookInventory.stream()
            .sorted(Comparator.comparing(Book::getPublicationDate).reversed())
            .collect(Collectors.toList());
    }
    
    public List<Book> getBooksSortedByPrice() {
        return bookInventory.stream()
            .sorted(Comparator.comparing(Book::getPrice))
            .collect(Collectors.toList());
    }
    
    public List<Book> getBooksSortedByAvailability() {
        return bookInventory.stream()
            .sorted(Comparator.comparing(book -> book.getStatus() == BookStatus.IN_STOCK ? 0 : 1))
            .collect(Collectors.toList());
    }
    
    public List<Order> getOrdersSortedByCompletionDate() {
        return orders.stream()
            .filter(order -> order.getCompletionDate() != null)
            .sorted(Comparator.comparing(Order::getCompletionDate).reversed())
            .collect(Collectors.toList());
    }
    
    public List<Order> getOrdersSortedByTotalPrice() {
        return orders.stream()
            .sorted(Comparator.comparing(Order::getTotalPrice).reversed())
            .collect(Collectors.toList());
    }
    
    public List<Order> getOrdersSortedByStatus() {
        return orders.stream()
            .sorted(Comparator.comparing(Order::getStatus))
            .collect(Collectors.toList());
    }
    
    public List<Request> getRequestsSortedByCount() {
        Map<String, Long> requestCounts = existingRequests.stream()
            .filter(request -> !request.isDone())
            .collect(Collectors.groupingBy(
                request -> request.getBook().getIsbn(),
                Collectors.counting()
            ));
        
        return existingRequests.stream()
            .filter(request -> !request.isDone())
            .sorted((r1, r2) -> Long.compare(
                requestCounts.getOrDefault(r2.getBook().getIsbn(), 0L),
                requestCounts.getOrDefault(r1.getBook().getIsbn(), 0L)
            ))
            .collect(Collectors.toList());
    }
    
    public List<Request> getRequestsSortedByBookTitle() {
        return existingRequests.stream()
            .filter(request -> !request.isDone())
            .sorted(Comparator.comparing(request -> request.getBook().getTitle()))
            .collect(Collectors.toList());
    }

    public List<Order> getCompletedOrdersInPeriod(Date startDate, Date endDate) {
        return orders.stream()
            .filter(order -> order.getStatus() == Order.OrderStatus.COMPLETED)
            .filter(order -> order.getCompletionDate() != null)
            .filter(order -> !order.getCompletionDate().before(startDate) && !order.getCompletionDate().after(endDate))
            .sorted(Comparator.comparing(Order::getCompletionDate).reversed())
            .collect(Collectors.toList());
    }
    
    public List<Order> getCompletedOrdersInPeriodSortedByPrice(Date startDate, Date endDate) {
        return getCompletedOrdersInPeriod(startDate, endDate).stream()
            .sorted(Comparator.comparing(Order::getTotalPrice).reversed())
            .collect(Collectors.toList());
    }
    
    public long getTotalRevenueInPeriod(Date startDate, Date endDate) {
        return getCompletedOrdersInPeriod(startDate, endDate).stream()
            .mapToLong(Order::getTotalPrice)
            .sum();
    }
    
    public int getCompletedOrdersCountInPeriod(Date startDate, Date endDate) {
        return getCompletedOrdersInPeriod(startDate, endDate).size();
    }
    
    public List<Book> getOldBooks() {
        Date sixMonthsAgo = Date.from(
            LocalDate.now().minus(6, ChronoUnit.MONTHS)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
        );
        Set<String> recentlySoldIsbns = orders.stream()
            .filter(order -> order.getStatus() == Order.OrderStatus.COMPLETED)
            .filter(order -> order.getCompletionDate() != null && order.getCompletionDate().after(sixMonthsAgo))
            .flatMap(order -> order.getBooks().stream())
            .map(Book::getIsbn)
            .collect(Collectors.toSet());
        
        return bookInventory.stream()
            .filter(book -> book.getStatus() == BookStatus.IN_STOCK)
            .filter(book -> !recentlySoldIsbns.contains(book.getIsbn()))
            .sorted(Comparator.comparing(Book::getArrivalDate))
            .collect(Collectors.toList());
    }
    
    public List<Book> getStaleBooksSortedByPrice() {
        return getOldBooks().stream()
            .sorted(Comparator.comparing(Book::getPrice).reversed())
            .collect(Collectors.toList());
    }
    
    public String getOrderDetails(int orderId) {
        Order order = findOrderById(orderId);
        if (order != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Заказ: ").append(orderId).append("\n");
            sb.append("Статус: ").append(order.getStatus()).append("\n");
            sb.append("Дата создания: ").append(order.getCreationDate()).append("\n");
            if (order.getCompletionDate() != null) {
                sb.append("Дата выполнения: ").append(order.getCompletionDate()).append("\n");
            }
            sb.append("Общая стоимость: ").append(order.getTotalPrice()).append(" руб.\n");
            sb.append("Книги в заказе:\n");
            order.getBooks().forEach(book -> 
                sb.append(" - ").append(book.getTitle()).append(" (").append(book.getAuthor())
                  .append(") - ").append(book.getPrice()).append(" руб.\n")
            );
            return sb.toString();
        } else {
            return "Заказ #" + orderId + " не найден";
        }
    }
    
    public String getBookDetails(String isbn) {
        Book book = findBookByIsbn(isbn);
        if (book != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Информация о книге:");
            sb.append("ISBN: ").append(book.getIsbn()).append("\n");
            sb.append("Название: ").append(book.getTitle()).append("\n");
            sb.append("Автор: ").append(book.getAuthor()).append("\n");
            sb.append("Цена: ").append(book.getPrice()).append(" руб.\n");
            sb.append("Статус: ").append(book.getStatus()).append("\n");
            sb.append("Дата публикации: ").append(book.getPublicationDate()).append("\n");
            sb.append("Дата поступления: ").append(book.getArrivalDate()).append("\n");
            return sb.toString();
        } else {
            return "Книга с ISBN " + isbn + " не найдена";
        }
    }
    
    private void loadInitialData() throws BookstoreException{
        try {
            importBooksFromCSV("books.csv");
            importOrdersFromCSV("orders.csv");
        } catch (BookstoreException e) {
        }
    }
    
    public void saveAllData() throws BookstoreException{
        try {
            exportBooksToCSV("books.csv");
            exportOrdersToCSV("orders.csv");
        } catch (BookstoreException e) {
        }
    }
}


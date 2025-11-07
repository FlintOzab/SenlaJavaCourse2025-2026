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
import t4.Order.OrderStatus;

public class Bookstore {
    private List<Book> bookInventory;
    private List<Request> existingRequests;
    private List<Order> orders;
    
    public Bookstore() {
        this.bookInventory = new ArrayList<>();
        this.existingRequests = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public void addBookToInventory(Book book) {
        bookInventory.add(book);
        book.setStatus(Book.BookStatus.IN_STOCK);
        fulfillBookRequests(book);
        System.out.println("Добавлена книга: " + book.getIsbn());
    }
    
    public void writeOffBook(String isbn) {
        bookInventory.stream()
            .filter(book -> book.getIsbn().equals(isbn))
            .findFirst()
            .ifPresent(book -> book.setStatus(Book.BookStatus.OUT_OF_STOCK));
    }
    
    public Order createOrder(int Id, List<Book> books) {
        Order order = new Order(Id, books);
        orders.add(order);
        order.getRequestsOnBooksOutOfStock(existingRequests);
        System.out.println("Создан заказ: " + order.getOrderId());
        if (order.containsOutOfStockBooks()) {
            System.out.println("В заказе есть отсутствующие книги. Созданы запросы.");
        }
        return order;
    }

    public void cancelOrder(int orderId) {
        Order order = findOrderById(orderId);
        if (order != null && order.getStatus() == Order.OrderStatus.NEW) {
            order.setStatus(Order.OrderStatus.CANCELLED);
            System.out.println("Заказ #" + orderId + " отменен");
        } else {
            System.out.println("Невозможно отменить заказ #" + orderId);
        }
    }
    
    public void updateOrderStatus(int orderId, Order.OrderStatus newStatus) {
        Order order = findOrderById(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            System.out.println("Статус заказа #" + orderId + " изменен на: " + newStatus);
        }
    }
    
    public void createBookRequest(Order order, Book book) {
        Request request = new Request(order, book);
        existingRequests.add(request);
        System.out.println("Создан запрос на книгу: '" + book.getIsbn());
    }
    
    public Order findOrderById(int orderId) {
        return orders.stream()
            .filter(order -> order.getOrderId() == orderId)
            .findFirst()
            .orElse(null);
    }
    
    public void fulfillBookRequests(Book book) {
        for (Request request : existingRequests) {
            if (!request.isDone() && request.matchesBook(book)) {
                request.setDone(true);
                System.out.println("Запрос #" + request.getRequestId() + " на книгу '" + book.getTitle() + "' выполнен");
            }
        }
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
            .sorted(Comparator.comparing(book -> book.getStatus() == Book.BookStatus.IN_STOCK ? 0 : 1))
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
    

    public List<Book> getStaleBooks() {
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
            .filter(book -> book.getStatus() == Book.BookStatus.IN_STOCK)
            .filter(book -> !recentlySoldIsbns.contains(book.getIsbn()))
            .sorted(Comparator.comparing(Book::getArrivalDate))
            .collect(Collectors.toList());
    }
    
    public List<Book> getStaleBooksSortedByPrice() {
        return getStaleBooks().stream()
            .sorted(Comparator.comparing(Book::getPrice).reversed())
            .collect(Collectors.toList());
    }
    
    public void displayOrderDetails(int orderId) {
        Order order = findOrderById(orderId);
        if (order != null) {
            System.out.println("Детали заказа" + orderId + " ===");
            System.out.println("Статус: " + order.getStatus());
            System.out.println("Дата создания: " + order.getCreationDate());
            if (order.getCompletionDate() != null) {
                System.out.println("Дата выполнения: " + order.getCompletionDate());
            }
            System.out.println("Общая стоимость: " + order.getTotalPrice() + " руб.");
            System.out.println("Книги в заказе:");
            order.getBooks().forEach(book -> 
                System.out.println(" - " + book.getTitle() + " (" + book.getAuthor() + ") - " + book.getPrice() + " руб.")
            );
        } else {
            System.out.println("Заказ #" + orderId + " не найден");
        }
    }
    
    public void displayBookDetails(String isbn) {
        bookInventory.stream()
            .filter(book -> book.getIsbn().equals(isbn))
            .findFirst()
            .ifPresentOrElse(
                book -> {
                    System.out.println("ISBN: " + book.getIsbn());
                    System.out.println("Название: " + book.getTitle());
                    System.out.println("Автор: " + book.getAuthor());
                    System.out.println("Цена: " + book.getPrice() + " руб.");
                    System.out.println("Статус: " + book.getStatus());
                    System.out.println("Дата публикации: " + book.getPublicationDate());
                    System.out.println("Дата поступления: " + book.getArrivalDate());
                },
                () -> System.out.println("Книга с ISBN " + isbn + " не найдена")
            );
    }
    
    public void displayAllBooks() {
        System.out.println("Список книг:");
        bookInventory.forEach(book -> 
            System.out.println(book.getTitle() + " - " + book.getAuthor() + " - " + book.getPrice() + " руб. - " + book.getStatus())
        );
    }
    
    public void displayAllOrders() {
        System.out.println("Список заказов:");
        orders.forEach(order -> 
            System.out.println("Заказ #" + order.getOrderId() + " - " + order.getStatus() + " - " + order.getTotalPrice() + " руб.")
        );
    }
    
    public void displayActiveRequests() {
        System.out.println("Список запросов:");
        existingRequests.stream()
            .filter(request -> !request.isDone())
            .forEach(request -> 
                System.out.println("Запрос #" + request.getRequestId() + " - " + request.getBook().getTitle())
            );
    }
}


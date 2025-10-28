package t4;

import java.util.ArrayList;
import java.util.List;

import t4.Book.BookStatus;
import t4.Order.OrderStatus;

public class Bookstore {
	private List<Book> bookInventory;
	private List<Request> existingRequests;
	private List<Order> orders;
	
	public Bookstore() {
		// TODO Auto-generated constructor stub
        this.bookInventory = new ArrayList<>();
        this.existingRequests = new ArrayList<>();
        this.orders = new ArrayList<>();
	}
	
	public void addBookToInventory(Book book) {
        bookInventory.add(book);
        book.setStatus(BookStatus.IN_STOCK);
        fulfillBookRequests(book);
        System.out.println("Добавлена книга: " + book.getIsbn());
    }
    
    public void writeOffBook(String isbn) {
    	bookInventory.stream().filter(book -> book.getIsbn().equals(isbn)).findFirst().orElseThrow(null).setStatus(BookStatus.OUT_OF_STOCK);
          
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
        if (order != null && order.getStatus() == OrderStatus.NEW) {
            order.setStatus(OrderStatus.CANCELLED);
            System.out.println("Заказ #" + orderId + " отменен");
        } else {
            System.out.println("Невозможно отменить заказ #" + orderId);
        }
    }
    
    public void updateOrderStatus(int orderId, OrderStatus newStatus) {
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
        return orders.stream().filter(order -> order.getOrderId() == orderId).findFirst().orElse(null);
    }
    
    public void fulfillBookRequests(Book book) {
        for (Request request : existingRequests) {
            if (!request.isDone() && request.matchesBook(book)) {
                request.setDone(true);
                System.out.println("Запрос #" + request.getRequestId() + " на книгу '" + book.getTitle() + "' выполнен");
            }
        }
    }
	
	
	
}

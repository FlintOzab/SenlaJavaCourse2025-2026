package t4;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShopApp {
	private final BookstoreController controller;
    private final UIFactory uiFactory;
    private final Menu menu;
    private final Display display;
    private final Input input;
    
    public ShopApp() {
        Bookstore service = new Bookstore();
        this.controller = new BookstoreController(service);
        this.uiFactory = new ConsoleUIFactory();
        this.menu = uiFactory.createMenu();
        this.display = uiFactory.createDisplay();
        this.input = uiFactory.createInput();
    }
    
    public void run() {
        while (true) {
            menu.showMainMenu();
            int choice = input.readInt("Выберите опцию");
            
            switch (choice) {
                case 1 -> handleBookManagement();
                case 2 -> handleOrderManagement();
                case 3 -> handleAnalytics();
                case 0 -> {
                    display.showMessage("До свидания!");
                    return;
                }
                default -> display.showError("Неверный выбор");
            }
        }
    }
    
    private void handleBookManagement() {
        while (true) {
            menu.showBookMenu();
            int choice = input.readInt("Выберите опцию");
            
            switch (choice) {
                case 1 -> addBook();
                case 2 -> viewBooks();
                case 3 -> viewBookDetails();
                case 4 -> writeOffBook();
                case 0 -> { return; }
                default -> display.showError("Неверный выбор");
            }
        }
    }
    
    private void handleOrderManagement() {
        while (true) {
            menu.showOrderMenu();
            int choice = input.readInt("Выберите опцию");
            
            switch (choice) {
                case 1 -> createOrder();
                case 2 -> viewOrders();
                case 3 -> viewOrderDetails();
                case 4 -> cancelOrder();
                case 5 -> updateOrderStatus();
                case 0 -> { return; }
                default -> display.showError("Неверный выбор");
            }
        }
    }
    
    private void handleAnalytics() {
        while (true) {
            menu.showAnalyticsMenu();
            int choice = input.readInt("Выберите опцию");
            
            switch (choice) {
                case 1 -> viewOldBooks();
                case 2 -> viewRevenue();
                case 3 -> viewCompletedOrders();
                case 0 -> { return; }
                default -> display.showError("Неверный выбор");
            }
        }
    }
    
    private void addBook() {
        try {
            String isbn = input.readString("ISBN");
            String title = input.readString("Название");
            String author = input.readString("Автор");
            long price = input.readLong("Цена");
            Date publicationDate = input.readDate("Дата публикации");
            Date arrivalDate = input.readDate("Дата поступления");
            String description = input.readString("Описание");
            
            Book book = new Book(isbn, title, author, price, publicationDate, arrivalDate, description);
            controller.addBook(book);
            display.showMessage("Книга успешно добавлена");
        } catch (Exception e) {
            display.showError("Ошибка при добавлении книги: " + e.getMessage());
        }
    }
    
    private void viewBooks() {
        List<Book> books = controller.getAllBooks();
        display.showList(books);
    }
    
    private void viewBookDetails() {
        String isbn = input.readString("Введите ISBN книги");
        String details = controller.getBookDetails(isbn);
        display.showMessage(details);
    }
    
    private void writeOffBook() {
        String isbn = input.readString("Введите ISBN книги для списания");
        try {
			controller.writeOffBook(isbn);
		} catch (BookstoreException e) {
			e.printStackTrace();
		}
        display.showMessage("Книга списана");
    }
    
    private void createOrder() {
        try {
            int orderId = input.readInt("ID заказа");
            List<Book> books = new ArrayList<>();
            books.addAll(controller.getAllBooks());
            Order order = controller.createOrder(orderId, books);
            display.showMessage("Заказ создан: " + order);
        } catch (Exception e) {
            display.showError("Ошибка при создании заказа: " + e.getMessage());
        }
    }
    
    private void viewOrders() {
        List<Order> orders = controller.getAllOrders();
        display.showList(orders);
    }
    
    private void viewOrderDetails() {
        int orderId = input.readInt("Введите ID заказа");
        String details = controller.getOrderDetails(orderId);
        display.showMessage(details);
    }
    
    private void cancelOrder() {
        int orderId = input.readInt("Введите ID заказа для отмены");
        try {
			controller.cancelOrder(orderId);
		} catch (BookstoreException e) {
			e.printStackTrace();
		}
        display.showMessage("Заказ отменен");
    }
    
    private void updateOrderStatus() {
        int orderId = input.readInt("Введите ID заказа");
        display.showMessage("Доступные статусы: NEW, COMPLETED, CANCELLED");
        String statusStr = input.readString("Введите новый статус");
        
        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr.toUpperCase());
            controller.updateOrderStatus(orderId, status);
            display.showMessage("Статус заказа обновлен");
        } catch (IllegalArgumentException | BookstoreException e) {
            display.showError("Неверный статус");
        }
    }
    
    private void viewOldBooks() {
        List<Book> staleBooks = controller.getOldBooks();
        display.showMessage("Залежавшиеся книги:");
        display.showList(staleBooks);
    }
    
    private void viewRevenue() {
        Date startDate = input.readDate("Начальная дата периода");
        Date endDate = input.readDate("Конечная дата периода");
        
        long revenue = controller.getTotalRevenueInPeriod(startDate, endDate);
        int ordersCount = controller.getCompletedOrdersCountInPeriod(startDate, endDate);
        
        display.showMessage(String.format(
            "Выручка за период: %d руб.\nКоличество завершенных заказов: %d", 
            revenue, ordersCount
        ));
    }
    
    private void viewCompletedOrders() {
        Date startDate = input.readDate("Начальная дата периода");
        Date endDate = input.readDate("Конечная дата периода");
        List<Order> orders = controller.getCompletedOrdersInPeriod(startDate, endDate);
        display.showMessage("Завершенные заказы за период:");
        display.showList(orders);
    }
    
}

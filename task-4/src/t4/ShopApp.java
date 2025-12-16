package t4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ShopApp {
	private BookstoreController controller;
    private final ConsoleMenuController menuController;
    private final Display display;
    private final Input input;
    
    public ShopApp() {
        this.display = ConsoleDisplay.getInstance();
        this.input = ConsoleInput.getInstance();
        this.menuController = new ConsoleMenuController(display, input);
        try {
            Bookstore bookstore = StateManager.loadState();
            if (bookstore == null) {
                bookstore = new Bookstore();
                display.showMessage("Создан новый книжный магазин");
            }

            this.controller = new BookstoreController(bookstore);
        } catch (BookstoreException e) {
            display.showError("Ошибка: " + e.getMessage());
            this.controller = new BookstoreController(new Bookstore());
        }
        
    }
    

    
    public void run() {
        try {
            Menu mainMenu = createMainMenu();
            menuController.execute(mainMenu);
        } finally {
            saveOnExit();
        }
    }
    
    private void saveOnExit() {
        try {
            StateManager.saveState(controller.getBookstore());
            controller.saveAllData();
            display.showMessage("Данные сохранены");
        } catch (Exception e) {
            display.showError("Ошибка при сохранении данных: " + e.getMessage());
        }
    }
    
    public Bookstore getBookstore() {
        return controller.getBookstore();
    }

    private Menu createMainMenu() {
	    return MenuBuilder.builder("Главное меню")
	            .addItem("Управление книгами", () -> menuController.execute(createBookMenu()))
	            .addItem("Управление заказами", () -> menuController.execute(createOrderMenu()))
	            .addItem("Дополнительные функции", () -> menuController.execute(createAnalyticsMenu()))
	            .addItem("Импорт/Экспорт данных", () -> menuController.execute(createImportExportMenu()))
	            .addItem("Управление конфигурацией", () -> menuController.execute(createConfigMenu()))
	            .addItem("Управление состоянием", () -> menuController.execute(createStateManagementMenu()))
	            .build();
	    }
    
    private Menu createBookMenu() {
        return MenuBuilder.builder("Управление книгами")
            .addItem("Добавить книгу", this::addBook)
            .addItem("Просмотр книг", this::viewBooks)
            .addItem("Детали книги", this::viewBookDetails)
            .addItem("Списать книгу", this::writeOffBook)
            .build();
    }
    
    private Menu createOrderMenu() {
        return MenuBuilder.builder("Управление заказами")
            .addItem("Создать заказ", this::createOrder)
            .addItem("Просмотр заказов", this::viewOrders)
            .addItem("Детали заказа", this::viewOrderDetails)
            .addItem("Отменить заказ", this::cancelOrder)
            .addItem("Завершить заказ", this::completeOrder)
            .addItem("Обновить статус заказа", this::updateOrderStatus)
            .build();
    }
    
    private Menu createAnalyticsMenu() {
        return MenuBuilder.builder("Дополнительные функции")
            .addItem("Просмотр залежавшихся книг", this::viewOldBooks)
            .addItem("Просмотр выручки", this::viewRevenue)
            .addItem("Завершенные заказы за период", this::viewCompletedOrders)
            .build();
    }
    
    private Menu createImportExportMenu() {
        return MenuBuilder.builder("Импорт/Экспорт данных")
            .addItem("Экспорт книг в CSV", this::exportBooks)
            .addItem("Импорт книг из CSV", this::importBooks)
            .addItem("Экспорт заказов в CSV", this::exportOrders)
            .addItem("Импорт заказов из CSV", this::importOrders)
            .build();
    }
    
    private Menu createConfigMenu() {
        return MenuBuilder.builder("Управление конфигурацией")
            .addItem("Просмотр текущих настроек", this::viewConfig)
            .addItem("Изменить порог залежавшихся книг", this::updateStaleThreshold)
            .addItem("Включить/выключить автоматическое выполнение заявок", this::toggleAutoFulfill)
            .addItem("Изменить директорию экспорта", this::updateExportDirectory)
            .addItem("Сохранить настройки", this::saveConfig)
            .build();
    }
    
    private Menu createStateManagementMenu() {
        return MenuBuilder.builder("Управление состоянием")
            .addItem("Создать резервную копию", this::createBackup)
            .addItem("Восстановить из резервной копии", this::restoreBackup)
            .addItem("Очистить состояние", this::clearState)
            .build();
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
        display.showMessage("Доступные книги (ID, Название, Автор, Цена, Статус):");
        books.forEach(book -> display.showMessage(
            String.format("ID: %d - %s, %s, %d руб., %s", 
                book.getId(), book.getTitle(), book.getAuthor(), 
                book.getPrice(), book.getStatus())
        ));
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
            display.showMessage("Книга списана");
        } catch (BookstoreException e) {
            display.showError("Ошибка при списании книги: " + e.getMessage());
        }
    }
    
    private void createOrder() {
        try {
            List<Book> availableBooks = controller.getAllBooks().stream()
                .filter(book -> book.getStatus() == Book.BookStatus.IN_STOCK)
                .collect(Collectors.toList());
            
            if (availableBooks.isEmpty()) {
                display.showError("Нет доступных книг для заказа");
                return;
            }
            
            display.showMessage("Доступные книги:");
            availableBooks.forEach(book -> display.showMessage(
                String.format("ID: %d - %s, %s, %d руб.", 
                    book.getId(), book.getTitle(), book.getAuthor(), book.getPrice())
            ));
            
            int orderNumber = input.readInt("Введите номер заказа");
            display.showMessage("Введите ID книг для заказа (через запятую):");
            String booksInput = input.readString("ID книг");
            String[] bookIdStrings = booksInput.split(",");
            List<Integer> bookIds = new ArrayList<>();
            
            for (String idStr : bookIdStrings) {
                try {
                    bookIds.add(Integer.parseInt(idStr.trim()));
                } catch (NumberFormatException e) {
                    display.showError("Некорректный ID книги: " + idStr);
                    return;
                }
            }
            
            Order order = controller.createOrder(orderNumber, bookIds);
            display.showMessage("Заказ создан: " + order);
        } catch (Exception e) {
            display.showError("Ошибка при создании заказа: " + e.getMessage());
        }
    }
    
    private void viewOrders() {
        List<Order> orders = controller.getAllOrders();
        if (orders.isEmpty()) {
            display.showMessage("Нет заказов");
        } else {
            display.showMessage("Все заказы:");
            display.showList(orders);
        }
    }
    
    private void viewOrderDetails() {
        int orderId = input.readInt("Введите номер заказа");
        String details = controller.getOrderDetails(orderId);
        display.showMessage(details);
    }
    
    private void cancelOrder() {
        int orderId = input.readInt("Введите номер заказа для отмены");
        try {
            controller.cancelOrder(orderId);
            display.showMessage("Заказ отменен");
        } catch (BookstoreException e) {
            display.showError("Ошибка при отмене заказа: " + e.getMessage());
        }
    }
    
    private void completeOrder() {
        int orderId = input.readInt("Введите номер заказа для завершения");
        try {
            controller.completeOrder(orderId);
            display.showMessage("Заказ успешно завершен");
        } catch (BookstoreException e) {
            display.showError("Ошибка при завершении заказа: " + e.getMessage());
        }
    }
    
    private void updateOrderStatus() {
        int orderId = input.readInt("Введите номер заказа");
        display.showMessage("Доступные статусы: NEW, COMPLETED, CANCELLED");
        String statusStr = input.readString("Введите новый статус");
        
        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr.toUpperCase());
            controller.updateOrderStatus(orderId, status);
            display.showMessage("Статус заказа обновлен");
        } catch (IllegalArgumentException e) {
            display.showError("Неверный статус");
        } catch (BookstoreException e) {
            display.showError("Ошибка обновления статуса: " + e.getMessage());
        }
    }
    
    private void viewOldBooks() {
        List<Book> staleBooks = controller.getOldBooks();
        if (staleBooks.isEmpty()) {
            display.showMessage("Нет залежавшихся книг");
        } else {
            display.showMessage("Залежавшиеся книги (более 6 месяцев без продаж):");
            display.showList(staleBooks);
        }
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
        
        if (orders.isEmpty()) {
            display.showMessage("Нет завершенных заказов за указанный период");
        } else {
            display.showMessage("Завершенные заказы за период:");
            display.showList(orders);
        }
    }
    
    private void exportBooks() {
        String filePath = input.readString("Введите путь для сохранения файла (например: books_export.csv)");
        try {
            controller.exportBooksToCSV(filePath);
            display.showMessage("Книги успешно экспортированы в " + filePath);
        } catch (BookstoreException e) {
            display.showError("Ошибка при экспорте книг: " + e.getMessage());
        }
    }
    
    private void importBooks() {
        String filePath = input.readString("Введите путь к файлу для импорта");
        try {
            controller.importBooksFromCSV(filePath);
            display.showMessage("Книги успешно импортированы из " + filePath);
        } catch (BookstoreException e) {
            display.showError("Ошибка при импорте книг: " + e.getMessage());
        }
    }
    
    private void exportOrders() {
        String filePath = input.readString("Введите путь для сохранения файла (например: orders_export.csv)");
        try {
            controller.exportOrdersToCSV(filePath);
            display.showMessage("Заказы успешно экспортированы в " + filePath);
        } catch (BookstoreException e) {
            display.showError("Ошибка при экспорте заказов: " + e.getMessage());
        }
    }
    
    private void importOrders() {
        String filePath = input.readString("Введите путь к файлу для импорта");
        try {
            controller.importOrdersFromCSV(filePath);
            display.showMessage("Заказы успешно импортированы из " + filePath);
        } catch (BookstoreException e) {
            display.showError("Ошибка при импорте заказов: " + e.getMessage());
        }
    }

    private void viewConfig() {
        BookstoreConfig config = controller.getConfig();
        display.showMessage("Текущие настройки:");
        display.showMessage("1. Порог залежавшихся книг: " + config.getStaleMonthsThreshold() + " месяцев");
        display.showMessage("2. Автоматическое выполнение заявок: " + 
                           (config.isAutoFulfillRequests() ? "Включено" : "Выключено"));
        display.showMessage("3. Директория экспорта: " + config.getExportDirectory());
    }

    private void updateStaleThreshold() {
        int threshold = input.readInt("Введите количество месяцев для порога залежавшихся книг");
        controller.getConfig().setStaleMonthsThreshold(threshold);
        display.showMessage("Порог обновлен");
    }

    private void toggleAutoFulfill() {
        boolean current = controller.getConfig().isAutoFulfillRequests();
        controller.getConfig().setAutoFulfillRequests(!current);
        display.showMessage("Автоматическое выполнение заявок: " + 
                           (!current ? "Включено" : "Выключено"));
    }

    private void updateExportDirectory() {
        String directory = input.readString("Введите новую директорию экспорта");
        controller.getConfig().setExportDirectory(directory);
        display.showMessage("Директория экспорта обновлена");
    }

    private void saveConfig() {
        controller.getConfig().savePropertiesToFile();
        display.showMessage("Настройки сохранены в файл");
    }
    private void createBackup() {
        try {
            StateManager.createBackup();
            display.showMessage("Резервная копия создана успешно");
        } catch (BookstoreException e) {
            display.showError("Ошибка создания резервной копии: " + e.getMessage());
        }
    }
    
    private void restoreBackup() {
        try {
            if (StateManager.restoreFromBackup()) {
                display.showMessage("Восстановление выполнено успешно. Перезапустите программу.");
            }
        } catch (BookstoreException e) {
            display.showError("Ошибка восстановления: " + e.getMessage());
        }
    }

    private void clearState() {
        StateManager.deleteState();
        display.showMessage("Сохраненное состояние очищено. Перезапустите программу.");
    }

}
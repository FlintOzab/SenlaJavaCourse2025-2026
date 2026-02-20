package app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import di.annotation.Component;
import di.annotation.Inject;
import config.BookstoreConfig;
import app.control.BookstoreController;
import app.control.ConsoleMenuController;
import util.LoggerUtil;
import exception.BookstoreException;
import model.Book;
import model.Order;
import service.StateManager;
import view.ConsoleDisplay;
import view.ConsoleInput;
import view.Menu;
import view.MenuBuilder;

/**
 * Main application class for the Bookstore.
 * Provides the user interface and menu navigation.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class ShopApp {
    
    /** Logger instance for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopApp.class);
    
    /** Date format pattern for user input. */
    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";
    
    /** The bookstore controller. */
    private final BookstoreController controller;
    
    /** The menu controller. */
    private final ConsoleMenuController menuController;
    
    /** The display component. */
    private final ConsoleDisplay display;
    
    /** The input component. */
    private final ConsoleInput input;
    
    /** The state manager. */
    private final StateManager stateManager;
    
    /**
     * Constructs a new ShopApp with the specified dependencies.
     * 
     * @param controller the bookstore controller
     * @param menuController the menu controller
     * @param display the display component
     * @param input the input component
     * @param stateManager the state manager
     */
    @Inject
    public ShopApp(final BookstoreController controller, 
                   final ConsoleMenuController menuController,
                   final ConsoleDisplay display, 
                   final ConsoleInput input,
                   final StateManager stateManager) {
        this.controller = controller;
        this.menuController = menuController;
        this.display = display;
        this.input = input;
        this.stateManager = stateManager;
        LOGGER.info("ShopApp initialized");
    }
    
    /**
     * Runs the main application loop.
     */
    public void run() {
        LOGGER.info("Starting ShopApp main loop");
        try {
            Menu mainMenu = createMainMenu();
            menuController.execute(mainMenu);
        } finally {
            saveOnExit();
        }
    }
    
    /**
     * Creates the main menu structure.
     * 
     * @return the main menu
     */
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
    
    /**
     * Creates the book management submenu.
     * 
     * @return the book menu
     */
    private Menu createBookMenu() {
        return MenuBuilder.builder("Управление книгами")
            .addItem("Добавить книгу", this::addBook)
            .addItem("Просмотр книг", this::viewBooks)
            .addItem("Детали книги", this::viewBookDetails)
            .addItem("Списать книгу", this::writeOffBook)
            .build();
    }
    
    /**
     * Creates the order management submenu.
     * 
     * @return the order menu
     */
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
    
    /**
     * Creates the analytics submenu.
     * 
     * @return the analytics menu
     */
    private Menu createAnalyticsMenu() {
        return MenuBuilder.builder("Дополнительные функции")
            .addItem("Просмотр залежавшихся книг", this::viewOldBooks)
            .addItem("Просмотр выручки", this::viewRevenue)
            .addItem("Завершенные заказы за период", this::viewCompletedOrders)
            .build();
    }
    
    /**
     * Creates the import/export submenu.
     * 
     * @return the import/export menu
     */
    private Menu createImportExportMenu() {
        return MenuBuilder.builder("Импорт/Экспорт данных")
            .addItem("Экспорт книг в CSV", this::exportBooks)
            .addItem("Импорт книг из CSV", this::importBooks)
            .addItem("Экспорт заказов в CSV", this::exportOrders)
            .addItem("Импорт заказов из CSV", this::importOrders)
            .build();
    }
    
    /**
     * Creates the configuration submenu.
     * 
     * @return the configuration menu
     */
    private Menu createConfigMenu() {
        return MenuBuilder.builder("Управление конфигурацией")
            .addItem("Просмотр текущих настроек", this::viewConfig)
            .addItem("Изменить порог залежавшихся книг", this::updateStaleThreshold)
            .addItem("Включить/выключить автоматическое выполнение заявок", this::toggleAutoFulfill)
            .addItem("Изменить директорию экспорта", this::updateExportDirectory)
            .addItem("Сохранить настройки", this::saveConfig)
            .build();
    }
    
    /**
     * Displays all books in the inventory.
     */
    private void viewBooks() {
        LOGGER.debug("Viewing all books");
        List<Book> books = controller.getAllBooks();
        display.showMessage("Доступные книги (ID, Название, Автор, Цена, Статус):");
        books.forEach(book -> display.showMessage(
            String.format("ID: %d - %s, %s, %d руб., %s", 
                book.getId(), book.getTitle(), book.getAuthor(), 
                book.getPrice(), book.getStatus())
        ));
    }
    
    /**
     * Displays details of a specific book.
     */
    private void viewBookDetails() {
        String isbn = input.readString("Введите ISBN книги");
        LOGGER.debug("Viewing book details for ISBN: {}", isbn);
        String details = controller.getBookDetails(isbn);
        display.showMessage(details);
    }
    
    /**
     * Writes off a book from inventory.
     */
    private void writeOffBook() {
        String isbn = input.readString("Введите ISBN книги для списания");
        LoggerUtil.logCommandStart(LOGGER, "writeOffBook", isbn);
        try {
            controller.writeOffBook(isbn);
            display.showMessage("Книга списана");
            LoggerUtil.logCommandSuccess(LOGGER, "writeOffBook", isbn);
        } catch (BookstoreException e) {
            display.showError("Ошибка при списании книги: " + e.getMessage());
            LoggerUtil.logCommandError(LOGGER, "writeOffBook", e);
        }
    }
    
    /**
     * Adds a new book to inventory.
     */
    private void addBook() {
        LoggerUtil.logCommandStart(LOGGER, "addBook");
        try {
            String isbn = input.readString("ISBN");
            String title = input.readString("Название");
            String author = input.readString("Автор");
            long price = input.readLong("Цена");
            Date publicationDate = input.readDate("Дата публикации (" + DATE_FORMAT_PATTERN + ")");
            Date arrivalDate = input.readDate("Дата поступления (" + DATE_FORMAT_PATTERN + ")");
            String description = input.readString("Описание");
            
            Book book = new Book(isbn, title, author, price, publicationDate, arrivalDate, description);
            controller.addBook(book);
            display.showMessage("Книга успешно добавлена в БД");
            LoggerUtil.logCommandSuccess(LOGGER, "addBook", book.getTitle());
        } catch (Exception e) {
            display.showError("Ошибка при добавлении книги: " + e.getMessage());
            LoggerUtil.logCommandError(LOGGER, "addBook", e);
        }
    }

    /**
     * Creates a new order.
     */
    private void createOrder() {
        LoggerUtil.logCommandStart(LOGGER, "createOrder");
        try {
            List<Book> availableBooks = controller.getAllBooks().stream()
                .filter(book -> book.getStatus() == Book.BookStatus.IN_STOCK)
                .collect(Collectors.toList());
            
            if (availableBooks.isEmpty()) {
                display.showError("Нет доступных книг для заказа");
                LOGGER.warn("Attempted to create order with no available books");
                return;
            }
            
            displayAvailableBooks(availableBooks);
            
            List<Integer> bookIds = readBookIds();
            if (bookIds.isEmpty()) {
                return;
            }
            
            Order order = controller.createOrder(bookIds);
            display.showMessage("Заказ создан в БД: " + order);
            LoggerUtil.logCommandSuccess(LOGGER, "createOrder", order.getId());
        } catch (Exception e) {
            display.showError("Ошибка при создании заказа: " + e.getMessage());
            LoggerUtil.logCommandError(LOGGER, "createOrder", e);
        }
    }
    
    /**
     * Displays available books for ordering.
     * 
     * @param availableBooks list of available books
     */
    private void displayAvailableBooks(final List<Book> availableBooks) {
        display.showMessage("Доступные книги:");
        availableBooks.forEach(book -> display.showMessage(
            String.format("ID: %d - %s, %s, %d руб.", 
                book.getId(), book.getTitle(), book.getAuthor(), book.getPrice())
        ));
    }
    
    /**
     * Reads book IDs from user input.
     * 
     * @return list of book IDs
     */
    private List<Integer> readBookIds() {
        display.showMessage("Введите ID книг для заказа (через запятую):");
        String booksInput = input.readString("ID книг");
        String[] bookIdStrings = booksInput.split(",");
        List<Integer> bookIds = new ArrayList<>();
        
        for (String idStr : bookIdStrings) {
            try {
                bookIds.add(Integer.parseInt(idStr.trim()));
            } catch (NumberFormatException e) {
                display.showError("Некорректный ID книги: " + idStr);
                LOGGER.error("Invalid book ID format: {}", idStr);
                return new ArrayList<>();
            }
        }
        return bookIds;
    }
    
    /**
     * Displays all orders.
     */
    private void viewOrders() {
        LOGGER.debug("Viewing all orders");
        List<Order> orders = controller.getAllOrders();
        if (orders.isEmpty()) {
            display.showMessage("Нет заказов");
        } else {
            display.showMessage("Все заказы:");
            display.showList(orders);
        }
    }
    
    /**
     * Displays details of a specific order.
     */
    private void viewOrderDetails() {
        int orderId = input.readInt("Введите номер заказа");
        LOGGER.debug("Viewing order details for ID: {}", orderId);
        String details = controller.getOrderDetails(orderId);
        display.showMessage(details);
    }
    
    /**
     * Cancels an order.
     */
    private void cancelOrder() {
        int orderId = input.readInt("Введите номер заказа для отмены");
        LoggerUtil.logCommandStart(LOGGER, "cancelOrder", orderId);
        try {
            controller.cancelOrder(orderId);
            display.showMessage("Заказ отменен");
            LoggerUtil.logCommandSuccess(LOGGER, "cancelOrder", orderId);
        } catch (BookstoreException e) {
            display.showError("Ошибка при отмене заказа: " + e.getMessage());
            LoggerUtil.logCommandError(LOGGER, "cancelOrder", e);
        }
    }
    
    /**
     * Completes an order.
     */
    private void completeOrder() {
        int orderId = input.readInt("Введите номер заказа для завершения");
        LoggerUtil.logCommandStart(LOGGER, "completeOrder", orderId);
        try {
            controller.completeOrder(orderId);
            display.showMessage("Заказ успешно завершен");
            LoggerUtil.logCommandSuccess(LOGGER, "completeOrder", orderId);
        } catch (BookstoreException e) {
            display.showError("Ошибка при завершении заказа: " + e.getMessage());
            LoggerUtil.logCommandError(LOGGER, "completeOrder", e);
        }
    }
    
    /**
     * Updates the status of an order.
     */
    private void updateOrderStatus() {
        int orderId = input.readInt("Введите номер заказа");
        display.showMessage("Доступные статусы: NEW, COMPLETED, CANCELLED");
        String statusStr = input.readString("Введите новый статус");
        
        LoggerUtil.logCommandStart(LOGGER, "updateOrderStatus", orderId, statusStr);
        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr.toUpperCase());
            controller.updateOrderStatus(orderId, status);
            display.showMessage("Статус заказа обновлен");
            LoggerUtil.logCommandSuccess(LOGGER, "updateOrderStatus", orderId);
        } catch (IllegalArgumentException e) {
            display.showError("Неверный статус");
            LOGGER.error("Invalid order status: {}", statusStr);
        } catch (BookstoreException e) {
            display.showError("Ошибка обновления статуса: " + e.getMessage());
            LoggerUtil.logCommandError(LOGGER, "updateOrderStatus", e);
        }
    }
    
    /**
     * Displays old books without recent sales.
     */
    private void viewOldBooks() {
        LOGGER.debug("Viewing old books");
        List<Book> staleBooks = controller.getOldBooks();
        if (staleBooks.isEmpty()) {
            display.showMessage("Нет залежавшихся книг");
        } else {
            display.showMessage("Залежавшиеся книги (более " 
                + controller.getConfig().getStaleMonthsThreshold() + " месяцев без продаж):");
            display.showList(staleBooks);
        }
    }
    
    /**
     * Displays revenue for a specified period.
     */
    private void viewRevenue() {
        Date startDate = input.readDate("Начальная дата периода (" + DATE_FORMAT_PATTERN + ")");
        Date endDate = input.readDate("Конечная дата периода (" + DATE_FORMAT_PATTERN + ")");
        
        LOGGER.debug("Viewing revenue for period: {} - {}", startDate, endDate);
        long revenue = controller.getTotalRevenueInPeriod(startDate, endDate);
        int ordersCount = controller.getCompletedOrdersCountInPeriod(startDate, endDate);
        
        display.showMessage(String.format(
            "Выручка за период: %d руб.\nКоличество завершенных заказов: %d", 
            revenue, ordersCount
        ));
    }
    
    /**
     * Displays completed orders for a specified period.
     */
    private void viewCompletedOrders() {
        Date startDate = input.readDate("Начальная дата периода (" + DATE_FORMAT_PATTERN + ")");
        Date endDate = input.readDate("Конечная дата периода (" + DATE_FORMAT_PATTERN + ")");
        
        LOGGER.debug("Viewing completed orders for period: {} - {}", startDate, endDate);
        List<Order> orders = controller.getCompletedOrdersInPeriod(startDate, endDate);
        
        if (orders.isEmpty()) {
            display.showMessage("Нет завершенных заказов за указанный период");
        } else {
            display.showMessage("Завершенные заказы за период:");
            display.showList(orders);
        }
    }
    
    /**
     * Exports books to CSV file.
     */
    private void exportBooks() {
        String filePath = input.readString("Введите путь для сохранения файла (например: books_export.csv)");
        LoggerUtil.logCommandStart(LOGGER, "exportBooks", filePath);
        try {
            controller.exportBooksToCSV(filePath);
            display.showMessage("Книги успешно экспортированы в " + filePath);
            LoggerUtil.logCommandSuccess(LOGGER, "exportBooks", filePath);
        } catch (BookstoreException e) {
            display.showError("Ошибка при экспорте книг: " + e.getMessage());
            LoggerUtil.logCommandError(LOGGER, "exportBooks", e);
        }
    }
    
    /**
     * Imports books from CSV file.
     */
    private void importBooks() {
        String filePath = input.readString("Введите путь к файлу для импорта");
        LoggerUtil.logCommandStart(LOGGER, "importBooks", filePath);
        try {
            controller.importBooksFromCSV(filePath);
            display.showMessage("Книги успешно импортированы из " + filePath);
            LoggerUtil.logCommandSuccess(LOGGER, "importBooks", filePath);
        } catch (BookstoreException e) {
            display.showError("Ошибка при импорте книг: " + e.getMessage());
            LoggerUtil.logCommandError(LOGGER, "importBooks", e);
        }
    }
    
    /**
     * Exports orders to CSV file.
     */
    private void exportOrders() {
        String filePath = input.readString("Введите путь для сохранения файла (например: orders_export.csv)");
        LoggerUtil.logCommandStart(LOGGER, "exportOrders", filePath);
        try {
            controller.exportOrdersToCSV(filePath);
            display.showMessage("Заказы успешно экспортированы в " + filePath);
            LoggerUtil.logCommandSuccess(LOGGER, "exportOrders", filePath);
        } catch (BookstoreException e) {
            display.showError("Ошибка при экспорте заказов: " + e.getMessage());
            LoggerUtil.logCommandError(LOGGER, "exportOrders", e);
        }
    }
    
    /**
     * Imports orders from CSV file.
     */
    private void importOrders() {
        String filePath = input.readString("Введите путь к файлу для импорта");
        LoggerUtil.logCommandStart(LOGGER, "importOrders", filePath);
        try {
            controller.importOrdersFromCSV(filePath);
            display.showMessage("Заказы успешно импортированы из " + filePath);
            LoggerUtil.logCommandSuccess(LOGGER, "importOrders", filePath);
        } catch (BookstoreException e) {
            display.showError("Ошибка при импорте заказов: " + e.getMessage());
            LoggerUtil.logCommandError(LOGGER, "importOrders", e);
        }
    }

    /**
     * Displays current configuration.
     */
    private void viewConfig() {
        LOGGER.debug("Viewing configuration");
        BookstoreConfig config = controller.getConfig();
        display.showMessage("Текущие настройки:");
        display.showMessage("1. Порог залежавшихся книг: " + config.getStaleMonthsThreshold() + " месяцев");
        display.showMessage("2. Автоматическое выполнение заявок: " 
            + (config.isAutoFulfillRequests() ? "Включено" : "Выключено"));
        display.showMessage("3. Директория экспорта: " + config.getExportDirectory());
    }

    /**
     * Updates the stale months threshold.
     */
    private void updateStaleThreshold() {
        int threshold = input.readInt("Введите количество месяцев для порога залежавшихся книг");
        LOGGER.debug("Updating stale threshold to: {}", threshold);
        controller.getConfig().setStaleMonthsThreshold(threshold);
        display.showMessage("Порог обновлен");
    }

    /**
     * Toggles auto-fulfillment of requests.
     */
    private void toggleAutoFulfill() {
        boolean current = controller.getConfig().isAutoFulfillRequests();
        controller.getConfig().setAutoFulfillRequests(!current);
        LOGGER.debug("Toggling auto-fulfill to: {}", !current);
        display.showMessage("Автоматическое выполнение заявок: " 
            + (!current ? "Включено" : "Выключено"));
    }

    /**
     * Updates the export directory.
     */
    private void updateExportDirectory() {
        String directory = input.readString("Введите новую директорию экспорта");
        LOGGER.debug("Updating export directory to: {}", directory);
        controller.getConfig().setExportDirectory(directory);
        display.showMessage("Директория экспорта обновлена");
    }

    /**
     * Saves configuration to file.
     */
    private void saveConfig() {
        LOGGER.debug("Saving configuration to file");
        controller.getConfig().savePropertiesToFile();
        display.showMessage("Настройки сохранены в файл");
    }
    
    /**
     * Saves data on application exit.
     */
    private void saveOnExit() {
        LOGGER.info("Saving data on exit");
        try {
            stateManager.saveState();
            controller.saveAllData();
            display.showMessage("Данные сохранены");
            LOGGER.info("Data saved successfully");
        } catch (Exception e) {
            if (display != null) {
                display.showError("Ошибка при сохранении данных: " + e.getMessage());
            } else {
                System.err.println("Ошибка при сохранении данных: " + e.getMessage());
            }
            LOGGER.error("Error saving data on exit", e);
        }
    }
    
    /**
     * Creates the state management submenu.
     * 
     * @return the state management menu
     */
    private Menu createStateManagementMenu() {
        return MenuBuilder.builder("Управление состоянием")
            .addItem("Создать резервную копию", this::createBackup)
            .addItem("Восстановить из резервной копии", this::restoreBackup)
            .addItem("Очистить состояние", this::clearState)
            .addItem("Информация о состоянии", this::showStateInfo)
            .build();
    }
    
    /**
     * Creates a backup of the current state.
     */
    private void createBackup() {
        LoggerUtil.logCommandStart(LOGGER, "createBackup");
        try {
            stateManager.createBackup();
            display.showMessage("Резервная копия создана успешно");
            LoggerUtil.logCommandSuccess(LOGGER, "createBackup", null);
        } catch (BookstoreException e) {
            display.showError("Ошибка создания резервной копии: " + e.getMessage());
            LoggerUtil.logCommandError(LOGGER, "createBackup", e);
        }
    }
    
    /**
     * Restores state from backup.
     */
    private void restoreBackup() {
        LoggerUtil.logCommandStart(LOGGER, "restoreBackup");
        try {
            if (stateManager.restoreFromBackup()) {
                display.showMessage("Восстановление выполнено успешно. Перезапустите программу.");
                LoggerUtil.logCommandSuccess(LOGGER, "restoreBackup", null);
            }
        } catch (BookstoreException e) {
            display.showError("Ошибка восстановления: " + e.getMessage());
            LoggerUtil.logCommandError(LOGGER, "restoreBackup", e);
        }
    }

    /**
     * Clears the saved state.
     */
    private void clearState() {
        LoggerUtil.logCommandStart(LOGGER, "clearState");
        stateManager.deleteState();
        display.showMessage("Сохраненное состояние очищено. Перезапустите программу.");
        LoggerUtil.logCommandSuccess(LOGGER, "clearState", null);
    }
    
    /**
     * Displays state information.
     */
    private void showStateInfo() {
        LOGGER.debug("Showing state info");
        try {
            boolean stateExists = stateManager.stateFileExists();
            boolean backupExists = stateManager.backupFileExists();
            
            StringBuilder info = new StringBuilder();
            info.append("Информация о состоянии:\n");
            info.append("Файл состояния: ").append(stateExists ? "существует" : "отсутствует").append("\n");
            info.append("Резервная копия: ").append(backupExists ? "существует" : "отсутствует").append("\n");
            
            display.showMessage(info.toString());
        } catch (Exception e) {
            display.showError("Ошибка получения информации: " + e.getMessage());
            LOGGER.error("Error getting state info", e);
        }
    }
}
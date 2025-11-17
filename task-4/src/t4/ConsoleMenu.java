package t4;

import java.util.Scanner;

public class ConsoleMenu implements Menu{
	enum MenuOption {
	    EXIT("Выход"),
	    BOOK_MANAGEMENT("Управление книгами"),
	    ORDER_MANAGEMENT("Управление заказами"),
	    ANALYTICS("Дополнительные функции"),
	    
	    ADD_BOOK("Добавить книгу"),
	    VIEW_BOOKS("Просмотр книг"),
	    VIEW_BOOK_DETAILS("Детали книги"),
	    WRITE_OFF_BOOK("Списать книгу"),
	    BACK("Назад"),
	    
	    CREATE_ORDER("Создать заказ"),
	    VIEW_ORDERS("Просмотр заказов"),
	    VIEW_ORDER_DETAILS("Детали заказа"),
	    CANCEL_ORDER("Отменить заказ"),
	    UPDATE_ORDER_STATUS("Обновить статус заказа"),
	
	    VIEW_OLD_BOOKS("Просмотр залежавшихся книг"),
	    VIEW_REVENUE("Просмотр выручки"),
	    VIEW_COMPLETED_ORDERS("Завершенные заказы за период");
	    
	    private final String description;
	    
	    MenuOption(String description) {
	        this.description = description;
	    }
	    
	    public String getDescription() {
	        return description;
	    }
	}
	private static ConsoleMenu instance;
    private final Scanner scanner;
    
    private ConsoleMenu() {
        this.scanner = new Scanner(System.in);
    }
    
    public static ConsoleMenu getInstance() {
        if (instance == null) {
            instance = new ConsoleMenu();
        }
        return instance;
    }
    
    @Override
    public void showMainMenu() {
        System.out.println("Главное меню");
        System.out.println("1. " + MenuOption.BOOK_MANAGEMENT.getDescription());
        System.out.println("2. " + MenuOption.ORDER_MANAGEMENT.getDescription());
        System.out.println("3. " + MenuOption.ANALYTICS.getDescription());
        System.out.println("0. " + MenuOption.EXIT.getDescription());
    }
    
    @Override
    public void showBookMenu() {
        System.out.println("Книги");
        System.out.println("1. " + MenuOption.ADD_BOOK.getDescription());
        System.out.println("2. " + MenuOption.VIEW_BOOKS.getDescription());
        System.out.println("3. " + MenuOption.VIEW_BOOK_DETAILS.getDescription());
        System.out.println("4. " + MenuOption.WRITE_OFF_BOOK.getDescription());
        System.out.println("0. " + MenuOption.BACK.getDescription());
    }
    
    @Override
    public void showOrderMenu() {
        System.out.println("Заказы");
        System.out.println("1. " + MenuOption.CREATE_ORDER.getDescription());
        System.out.println("2. " + MenuOption.VIEW_ORDERS.getDescription());
        System.out.println("3. " + MenuOption.VIEW_ORDER_DETAILS.getDescription());
        System.out.println("4. " + MenuOption.CANCEL_ORDER.getDescription());
        System.out.println("5. " + MenuOption.UPDATE_ORDER_STATUS.getDescription());
        System.out.println("0. " + MenuOption.BACK.getDescription());
    }
    
    @Override
    public void showAnalyticsMenu() {
        System.out.println("Доп. функции");
        System.out.println("1. " + MenuOption.VIEW_OLD_BOOKS.getDescription());
        System.out.println("2. " + MenuOption.VIEW_REVENUE.getDescription());
        System.out.println("3. " + MenuOption.VIEW_COMPLETED_ORDERS.getDescription());
        System.out.println("0. " + MenuOption.BACK.getDescription());
    }
}

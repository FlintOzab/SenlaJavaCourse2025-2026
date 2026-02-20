package view;

import di.annotation.Component;

import java.util.List;

/**
 * Console implementation of the Display interface.
 * Outputs messages to the system console.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class ConsoleDisplay implements Display {
    
    /** Error message prefix. */
    private static final String ERROR_PREFIX = "Error: ";
    
    /** Empty list message. */
    private static final String EMPTY_LIST_MESSAGE = "Список пуст";
    
    /**
     * Default constructor.
     */
    public ConsoleDisplay() {
        // Default constructor
    }
    
    @Override
    public void showMessage(final String message) {
        System.out.println(message);
    }
    
    @Override
    public void showError(final String error) {
        System.out.println(ERROR_PREFIX + error);
    }
    
    @Override
    public void showList(final List<?> items) {
        if (items == null || items.isEmpty()) {
            System.out.println(EMPTY_LIST_MESSAGE);
        } else {
            items.forEach(System.out::println);
        }
    }
}
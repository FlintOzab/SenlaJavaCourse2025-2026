package bookstore.view;

import java.util.List;

/**
 * Console implementation of the Menu class.
 * Provides console-based menu display and input processing.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class ConsoleMenu extends Menu {
    
    /**
     * Constructs a new ConsoleMenu with the specified title and items.
     * 
     * @param title the menu title
     * @param items the menu items
     */
    public ConsoleMenu(final String title, final List<MenuItem> items) {
        super(title, items);
    }
    
    @Override
    public void display() {
        // Implementation for displaying the menu
        // This method is intended to be overridden or implemented
        // by specific menu display logic
    }
    
    @Override
    public void processInput(final int choice) {
        // Implementation for processing user input
        // This method is intended to be overridden or implemented
        // by specific menu processing logic
    }
}
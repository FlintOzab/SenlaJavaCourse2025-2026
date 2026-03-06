package bookstore.view;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for menus.
 * Provides common menu structure and operations.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public abstract class Menu {
    
    /** Menu title. */
    protected final String title;
    
    /** Menu items. */
    protected final List<MenuItem> items;
    
    /**
     * Constructs a new Menu with the specified title and items.
     * 
     * @param title the menu title
     * @param items the menu items
     */
    public Menu(final String title, final List<MenuItem> items) {
        this.title = title;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }
    
    /**
     * Gets the menu title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Gets the menu items.
     * 
     * @return the items
     */
    public List<MenuItem> getItems() {
        return new ArrayList<>(items);
    }
    
    /**
     * Displays the menu.
     */
    public abstract void display();
    
    /**
     * Processes user input.
     * 
     * @param choice the user's choice
     */
    public abstract void processInput(int choice);
}
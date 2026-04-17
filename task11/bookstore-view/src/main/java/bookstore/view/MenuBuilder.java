package bookstore.view;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for creating menus.
 * Provides a fluent interface for building menus with items.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public final class MenuBuilder {
    
    /** Menu title. */
    private final String title;
    
    /** Menu items being built. */
    private final List<MenuItem> items;
    
    /**
     * Private constructor, use builder() method instead.
     * 
     * @param title the menu title
     */
    private MenuBuilder(final String title) {
        this.title = title;
        this.items = new ArrayList<>();
    }
    
    /**
     * Creates a new MenuBuilder instance.
     * 
     * @param title the menu title
     * @return a new MenuBuilder
     */
    public static MenuBuilder builder(final String title) {
        return new MenuBuilder(title);
    }
    
    /**
     * Adds a menu item with a command.
     * 
     * @param name the item name
     * @param command the command to execute
     * @return this builder
     */
    public MenuBuilder addItem(final String name, final Command command) {
        items.add(MenuItem.of(name, command));
        return this;
    }
    
    /**
     * Adds a submenu item.
     * 
     * @param name the item name
     * @param menu the submenu
     * @return this builder
     */
    public MenuBuilder addSubmenu(final String name, final Menu menu) {
        // Create a command that executes the submenu
        Command submenuCommand = () -> {
            // This would typically be handled by the menu controller
            // For now, just an empty command
        };
        items.add(MenuItem.of(name, submenuCommand));
        return this;
    }
    
    /**
     * Builds the menu.
     * 
     * @return the constructed menu
     */
    public Menu build() {
        return new ConsoleMenu(title, new ArrayList<>(items));
    }
}
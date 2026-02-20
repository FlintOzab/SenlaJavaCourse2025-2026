package view;

/**
 * Represents a single item in a menu.
 * Contains a name and an associated command.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class MenuItem {
    
    /** Item display name. */
    private final String name;
    
    /** Command to execute when selected. */
    private final Command command;
    
    /**
     * Constructs a new MenuItem.
     * 
     * @param name the item name
     * @param command the associated command
     */
    public MenuItem(final String name, final Command command) {
        this.name = name;
        this.command = command;
    }
    
    /**
     * Gets the item name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Executes the associated command.
     */
    public void execute() {
        if (command != null) {
            command.execute();
        }
    }
    
    /**
     * Factory method for creating menu items.
     * 
     * @param name the item name
     * @param command the associated command
     * @return a new MenuItem
     */
    public static MenuItem of(final String name, final Command command) {
        return new MenuItem(name, command);
    }
}
package bookstore.view;

/**
 * Command interface for the command pattern.
 * Represents an executable action in the menu system.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public interface Command {
    
    /**
     * Executes the command.
     */
    void execute();
}
package view;

import java.util.List;

/**
 * Display interface for output operations.
 * Defines methods for showing messages, errors, and lists.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public interface Display {
    
    /**
     * Shows a message to the user.
     * 
     * @param message the message to show
     */
    void showMessage(String message);
    
    /**
     * Shows an error message to the user.
     * 
     * @param error the error message to show
     */
    void showError(String error);
    
    /**
     * Shows a list of items to the user.
     * 
     * @param items the list of items to show
     */
    void showList(List<?> items);
}
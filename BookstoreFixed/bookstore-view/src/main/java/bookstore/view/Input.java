package bookstore.view;

import java.util.Date;

/**
 * Input interface for reading user input.
 * Defines methods for reading various data types.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public interface Input {
    
    /**
     * Reads a string from the user.
     * 
     * @param prompt the prompt to display
     * @return the entered string
     */
    String readString(String prompt);
    
    /**
     * Reads an integer from the user.
     * 
     * @param prompt the prompt to display
     * @return the entered integer
     */
    int readInt(String prompt);
    
    /**
     * Reads a long integer from the user.
     * 
     * @param prompt the prompt to display
     * @return the entered long
     */
    long readLong(String prompt);
    
    /**
     * Reads a date from the user.
     * 
     * @param prompt the prompt to display
     * @return the entered date
     */
    Date readDate(String prompt);
}
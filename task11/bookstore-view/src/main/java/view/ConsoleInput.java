package view;

import di.annotation.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Console implementation of the Input interface.
 * Reads user input from the system console.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class ConsoleInput implements Input {
    
    /** Date format pattern for user input. */
    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";
    
    /** Error message for invalid integer input. */
    private static final String ERROR_INTEGER = "Ошибка: введите целое число";
    
    /** Error message for invalid number input. */
    private static final String ERROR_NUMBER = "Ошибка: введите число";
    
    /** Error message for invalid date input. */
    private static final String ERROR_DATE = 
        "Ошибка: введите дату в формате " + DATE_FORMAT_PATTERN;
    
    /** Scanner for reading input. */
    private final Scanner scanner;
    
    /** Date formatter for parsing dates. */
    private final SimpleDateFormat dateFormat;
    
    /**
     * Default constructor.
     */
    public ConsoleInput() {
        this.scanner = new Scanner(System.in);
        this.dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    }
    
    @Override
    public String readString(final String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine();
    }
    
    @Override
    public int readInt(final String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(ERROR_INTEGER);
            }
        }
    }
    
    @Override
    public long readLong(final String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(ERROR_NUMBER);
            }
        }
    }
    
    @Override
    public Date readDate(final String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " (" + DATE_FORMAT_PATTERN + "): ");
                String dateStr = scanner.nextLine();
                return dateFormat.parse(dateStr);
            } catch (ParseException e) {
                System.out.println(ERROR_DATE);
            }
        }
    }
}
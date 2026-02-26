package app.control;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import view.ConsoleDisplay;
import view.ConsoleInput;
import view.Menu;
import view.MenuItem;
import util.LoggerUtil;

/**
 * Controller for managing console menus.
 * Handles menu navigation and user input processing.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class ConsoleMenuController {
    
    /** Logger instance for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleMenuController.class);
    
    /** The display for showing messages. */
    private ConsoleDisplay display;
    
    /** The input handler for reading user input. */
    private ConsoleInput input;
    
    /** Stack of menus for navigation. */
    private List<Menu> menuStack;
    
    /**
     * Constructs a new ConsoleMenuController with the specified dependencies.
     * 
     * @param display the display component
     * @param input the input component
     */
    @Autowired
    public ConsoleMenuController(ConsoleDisplay display, ConsoleInput input) {
        this.display = display;
        this.input = input;
        this.menuStack = new ArrayList<>();
        LOGGER.info("ConsoleMenuController initialized");
    }
    
    /**
     * Executes a menu and pushes it onto the navigation stack.
     * 
     * @param menu the menu to execute
     */
    public void execute(final Menu menu) {
        LoggerUtil.logCommandStart(LOGGER, "executeMenu", menu.getTitle());
        menuStack.add(menu);
        runCurrentMenu();
    }
    
    /**
     * Runs the current menu loop until navigation back or exit.
     */
    private void runCurrentMenu() {
        while (!menuStack.isEmpty()) {
            Menu currentMenu = menuStack.get(menuStack.size() - 1);
            displayMenu(currentMenu);
            
            int choice = input.readInt("Выберите опцию") - 1;
            
            if (choice == -1) {
                if (handleExitOption()) {
                    return;
                }
                continue;
            }
            
            processMenuChoice(currentMenu, choice);
        }
    }
    
    /**
     * Displays the menu options.
     * 
     * @param menu the menu to display
     */
    private void displayMenu(final Menu menu) {
        display.showMessage("\n" + menu.getTitle());
        String separator = "=".repeat(menu.getTitle().length());
        display.showMessage(separator);
        
        List<MenuItem> items = menu.getItems();
        for (int i = 0; i < items.size(); i++) {
            display.showMessage((i + 1) + ". " + items.get(i).getName());
        }
        
        if (menuStack.size() > 1) {
            display.showMessage("0. Назад");
        } else {
            display.showMessage("0. Выход");
        }
    }
    
    /**
     * Handles the exit/back option.
     * 
     * @return true if the application should exit, false otherwise
     */
    private boolean handleExitOption() {
        if (menuStack.size() > 1) {
            LOGGER.debug("Navigating back to previous menu");
            menuStack.remove(menuStack.size() - 1);
            return false;
        } else {
            LOGGER.info("Exiting application");
            menuStack.clear();
            return true;
        }
    }
    
    /**
     * Processes the user's menu choice.
     * 
     * @param menu the current menu
     * @param choice the user's choice (0-based index)
     */
    private void processMenuChoice(final Menu menu, final int choice) {
        List<MenuItem> items = menu.getItems();
        
        if (choice >= 0 && choice < items.size()) {
            MenuItem selectedItem = items.get(choice);
            LOGGER.debug("Executing menu item: {}", selectedItem.getName());
            selectedItem.execute();
        } else {
            LOGGER.warn("Invalid menu choice: {}", choice + 1);
            display.showError("Неверный выбор");
        }
    }
}
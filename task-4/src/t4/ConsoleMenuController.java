package t4;

import java.util.ArrayList;
import java.util.List;

public class ConsoleMenuController {
    private final Display display;
    private final Input input;
    private final List<Menu> menuStack = new ArrayList<>();
    
    public ConsoleMenuController(Display display, Input input) {
        this.display = display;
        this.input = input;
    }
    
    public void execute(Menu menu) {
        menuStack.add(menu);
        runCurrentMenu();
    }
    
    private void runCurrentMenu() {
        while (!menuStack.isEmpty()) {
            Menu currentMenu = menuStack.get(menuStack.size() - 1);
            display.showMessage("\n" + currentMenu.getTitle());
            display.showMessage("=".repeat(currentMenu.getTitle().length()));
            List<MenuItem> items = currentMenu.getItems();
            for (int i = 0; i < items.size(); i++) {
                display.showMessage((i + 1) + ". " + items.get(i).getName());
            }
            if (menuStack.size() > 1) {
                display.showMessage("0. Назад");
            } else {
                display.showMessage("0. Выход");
            }
            
            int choice = input.readInt("Выберите опцию") - 1;
            if (choice == -1) {
                if (menuStack.size() > 1) {
                    menuStack.remove(menuStack.size() - 1);
                    continue;
                } else {
                    menuStack.clear();
                    return;
                }
            }
            if (choice >= 0 && choice < items.size()) {
                MenuItem selectedItem = items.get(choice);
                selectedItem.execute();
            } else {
                display.showError("Неверный выбор");
            }
        }
    }
}

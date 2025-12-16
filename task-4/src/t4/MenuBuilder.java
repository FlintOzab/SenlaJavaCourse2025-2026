package t4;

import java.util.ArrayList;
import java.util.List;

public class MenuBuilder {
    private final String title;
    private final List<MenuItem> items = new ArrayList<>();
    
    private MenuBuilder(String title) {
        this.title = title;
    }
    
    public static MenuBuilder builder(String title) {
        return new MenuBuilder(title);
    }
    
    public MenuBuilder addItem(String name, Command command) {
        items.add(MenuItem.of(name, command));
        return this;
    }
    
    public MenuBuilder addSubmenu(String name, Menu menu) {
        items.add(MenuItem.of(name, () -> {}));
        return this;
    }
    
    public Menu build() {
        return new ConsoleMenu(title, new ArrayList<>(items));
    }
}
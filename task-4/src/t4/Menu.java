package t4;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {
    protected final String title;
    protected final List<MenuItem> items;
    
    public Menu(String title, List<MenuItem> items) {
        this.title = title;
        this.items = items;
    }
    
    public String getTitle() {
        return title;
    }
    
    public List<MenuItem> getItems() {
        return items;
    }
    
    public abstract void display();
    public abstract void processInput(int choice);
}

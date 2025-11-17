package t4;

import java.util.List;

public class ConsoleDisplay implements Display {

private static ConsoleDisplay instance;
    
    private ConsoleDisplay() {}
    
    public static ConsoleDisplay getInstance() {
        if (instance == null) {
            instance = new ConsoleDisplay();
        }
        return instance;
    }
    
    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }
    
    @Override
    public void showError(String error) {
        System.out.println("Error: " + error);
    }
    
    @Override
    public void showList(List<?> items) {
        if (items.isEmpty()) {
            System.out.println("Список пуст");
        } else {
            items.forEach(System.out::println);
        }
    }

}

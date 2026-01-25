package t4.view;

import java.util.List;

import di.annotation.Component;

@Component
public class ConsoleDisplay implements Display {
    
    public ConsoleDisplay() {}
    
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

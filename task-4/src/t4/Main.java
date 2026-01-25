package t4;

import t4.config.BookstoreConfig;
import t4.config.ConfigModule;
import t4.control.BookstoreController;
import t4.control.ConsoleMenuController;
import di.DependencyInjector;
import t4.service.*;
import t4.view.*;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        try {
            Bookstore loadedBookstore = StateManager.loadState();
            
            DependencyInjector container = createContainer(loadedBookstore);
            
            autoConfigureComponents(container);
            
            ShopApp app = container.getComponent(ShopApp.class);
            app.run();
            
        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static DependencyInjector createContainer(Bookstore loadedBookstore) {
    	DependencyInjector.Builder builder = DependencyInjector.builder();
        
        if (loadedBookstore != null) {
            System.out.println("Состояние загружено из файла");
            builder.withComponentInstance(Bookstore.class, loadedBookstore);
            builder.withComponent(BookstoreConfig.class)
                   .withComponent(CSVService.class);
        } else {
            System.out.println("Создание нового магазина");
            builder.withComponent(BookstoreConfig.class)
                   .withComponent(CSVService.class)
                   .withComponent(Bookstore.class);
        }
        
        builder.withComponent(ConsoleDisplay.class)
               .withComponent(ConsoleInput.class)
               .withComponent(ConsoleMenuController.class)
               .withComponent(StateManager.class) 
               .withComponent(BookstoreController.class)
               .withComponent(ShopApp.class); 
        
        return builder.build();
    }
    
    private static void autoConfigureComponents(DependencyInjector container) {
        try {
            Set<Object> allInstances = container.getAllInstances();
            
            for (Object instance : allInstances) {
                try {
                    if (ConfigModule.hasConfigProperties(instance)) {
                        System.out.println("Конфигурирование: " + instance.getClass().getName());
                        ConfigModule.configure(instance);
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка конфигурации " + 
                        instance.getClass().getName() + ": " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Ошибка автоматической конфигурации: " + e.getMessage());
        }
    }
}
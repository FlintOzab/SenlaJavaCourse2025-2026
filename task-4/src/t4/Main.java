package t4;

class Main {
    public static void main(String[] args) {
    	try {
            Bookstore bookstore = null;
            try {
                bookstore = StateManager.loadState();
            } catch (BookstoreException e) {
                System.err.println("Ошибка загрузки состояния: " + e.getMessage());
            }
            
            System.out.println("Инициализация DI контейнера...");
            DependencyInjector.initialize("t4");
            
            if (bookstore == null) {
                System.out.println("Создание нового магазина...");
                bookstore = DependencyInjector.getComponent(Bookstore.class);
            }
            
            BookstoreController controller = new BookstoreController(bookstore);
            DependencyInjector.injectFieldDependencies(controller);
            
            System.out.println("Создание приложения...");
            ShopApp app = DependencyInjector.getComponent(ShopApp.class);
            
            if (app == null) {
                System.err.println("Не удалось создать приложение!");
                return;
            }
            
            System.out.println("Приложение успешно создано, запуск...");
            app.run();
            
        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

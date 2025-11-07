package t4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Main {
	public static void main(String[] args) throws ParseException {
		Bookstore store = new Bookstore();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date pubDate1 = sdf.parse("1967-01-01");
        Date pubDate2 = sdf.parse("1949-01-01");
        Date pubDate3 = sdf.parse("1936-01-01");
        Date arrivalDate1 = sdf.parse("2025-08-01");
        Date arrivalDate2 = sdf.parse("2024-07-01");
        Date arrivalDate3 = sdf.parse("2025-07-01");
        Book book1 = new Book("978-5-389-07435-4", "Мастер и Маргарита", "Михаил Булгаков", (long) 450, pubDate1, arrivalDate1,  "Описание 1");
        Book book2 = new Book("978-5-17-087885-8", "1984", "Джордж Оруэлл", (long) 380, pubDate2, arrivalDate2, "Описание 2");
        Book book3 = new Book("978-5-699-97388-5", "Три товарища", "Эрих Мария Ремарк", (long) 520, pubDate3, arrivalDate3, "Описание 3");
        Date pubDate4 = sdf.parse("1866-01-01");
        Date arrivalDate4 = sdf.parse("2024-01-01");
        Book book4 = new Book("978-5-04-103852-9", "Преступление и наказание", "Федор Достоевский", (long) 420, pubDate4, arrivalDate4, "Классика русской литературы");
        
        store.addBookToInventory(book1);
        store.addBookToInventory(book2);
        store.addBookToInventory(book3);
        store.addBookToInventory(book4);
      
        List<Book> order1Books = new ArrayList<>();
        order1Books.add(book1);
        order1Books.add(book2);
        Order order1 = store.createOrder(1, order1Books);
        order1.completeOrder();
        
        List<Book> order2Books = new ArrayList<>();
        order2Books.add(book3);
        Order order2 = store.createOrder(2, order2Books);
        order2.completeOrder();
     
        List<Book> order3Books = new ArrayList<>();
        order3Books.add(book4);
        Order order3 = store.createOrder(3, order3Books);
        Book requestedBook = new Book("978-5-271-46123-1", "Сто лет одиночества", "Габриэль Гарсиа Маркес",  (long) 610, sdf.parse("1967-05-30"), arrivalDate2, "Desc5");
        store.createBookRequest(order3, requestedBook);
        store.createBookRequest(order3, requestedBook); // Дублирующий запрос для теста количества

        System.out.println("Сортировка по алфавиту:");
        List<Book> booksByTitle = store.getBooksSortedByTitle();
        booksByTitle.forEach(book -> System.out.println(" - " + book.getTitle()));

        System.out.println("Сортировка книг по дате публикации:");
        List<Book> booksByPubDate = store.getBooksSortedByPublicationDate();
        booksByPubDate.forEach(book -> 
            System.out.println(" - " + book.getTitle() + " (" + sdf.format(book.getPublicationDate()) + ")")
        );
        System.out.println("Сортировка книг по цене:");
        List<Book> booksByPrice = store.getBooksSortedByPrice();
        booksByPrice.forEach(book -> 
            System.out.println(" - " + book.getTitle() + " - " + book.getPrice() + " руб.")
        );

        System.out.println("Сортировка книг по наличию");
        store.writeOffBook("978-5-699-97388-5");
        List<Book> booksByAvailability = store.getBooksSortedByAvailability();
        booksByAvailability.forEach(book -> 
            System.out.println(" - " + book.getTitle() + " - " + book.getStatus())
        );
        System.out.println("Заказы по дате выполнения:");
        List<Order> ordersByCompletion = store.getOrdersSortedByCompletionDate();
        ordersByCompletion.forEach(order -> 
            System.out.println(" - Заказ #" + order.getOrderId() + " - " + 
                (order.getCompletionDate() != null ? sdf.format(order.getCompletionDate()) : "Не выполнен"))
        );
        System.out.println("Сортировка заказов по общей стоимости:");
        List<Order> ordersByPrice = store.getOrdersSortedByTotalPrice();
        ordersByPrice.forEach(order -> 
            System.out.println(" - Заказ #" + order.getOrderId() + " - " + order.getTotalPrice() + " руб.")
        );
 
        System.out.println("Сортировка заказов по статусу:");
        List<Order> ordersByStatus = store.getOrdersSortedByStatus();
        ordersByStatus.forEach(order -> 
            System.out.println(" - Заказ #" + order.getOrderId() + " - " + order.getStatus())
        );

        System.out.println("Выполненные заказы за период:");
        Date startDate = sdf.parse("2024-01-01");
        Date endDate = sdf.parse("2025-12-31");
        List<Order> completedOrders = store.getCompletedOrdersInPeriod(startDate, endDate);
        System.out.println("Период: " + sdf.format(startDate) + " - " + sdf.format(endDate));
        completedOrders.forEach(order -> 
            System.out.println(" - Заказ #" + order.getOrderId() + " - " + 
                sdf.format(order.getCompletionDate()) + " - " + order.getTotalPrice() + " руб.")
        );


        System.out.println("Заработок за период:");
        long totalRevenue = store.getTotalRevenueInPeriod(startDate, endDate);
        System.out.println("Общая выручка за период: " + totalRevenue + " руб.");

        System.out.println("Список залежавшихся книг (не проданы больше 6 месяцев)");
        List<Book> staleBooks = store.getStaleBooks();
        if (staleBooks.isEmpty()) {
            System.out.println("Залежавшихся книг нет");
        } else {
            staleBooks.forEach(book -> 
                System.out.println(" - " + book.getTitle() + " - поступила: " + 
                    sdf.format(book.getArrivalDate()) + " - " + book.getPrice() + " руб.")
            );
        }
        store.displayOrderDetails(1);   
        store.displayBookDetails("978-5-389-07435-4"); 
        store.displayAllBooks();
        store.displayAllOrders();
        store.displayActiveRequests();

    }
}


package t4;

import java.util.ArrayList;
import java.util.List;

class Main {
	public static void main(String[] args) {
        
        Bookstore bookstore = new Bookstore();
        Book book1 = new Book("978-5-389-07435-4", "Мастер и Маргарита", "Михаил Булгаков", 450.0f);
        Book book2 = new Book("978-5-17-087885-8", "1984", "Джордж Оруэлл", 380.0f);
        Book book3 = new Book("978-5-699-97388-5", "Три товарища", "Эрих Мария Ремарк", 520.0f);
        bookstore.addBookToInventory(book1);
        bookstore.addBookToInventory(book2);
        bookstore.addBookToInventory(book3);
     
        System.out.println("Статус книги 'Три товарища' до списания: " + book3.getStatus());
        bookstore.writeOffBook("978-5-699-97388-5");
        System.out.println("Статус книги 'Три товарища' после списания: " + book3.getStatus()); 
        List<Book> order1Books = new ArrayList<>();
        order1Books.add(book1);
        order1Books.add(book2);
        Order order1 = bookstore.createOrder(1, order1Books);
        System.out.println("Статус заказа 1: " + order1.getStatus());
        System.out.println("Содержит отсутствующие книги: " + order1.containsOutOfStockBooks());
        List<Book> order2Books = new ArrayList<>();
        order2Books.add(book3);
        order2Books.add(book1); 
        Order order2 = bookstore.createOrder(2, order2Books);
        System.out.println("Статус заказа 2: " + order2.getStatus());
        System.out.println("Содержит отсутствующие книги: " + order2.containsOutOfStockBooks());
        List<Book> outOfStockBooks = order2.getOutOfStockBooks();
        System.out.println("Отсутствующие книги в заказе 2:");
        for (Book book : outOfStockBooks) {
            System.out.println(" - " + book.getTitle() + " (ISBN: " + book.getIsbn() + ")");
        }
        System.out.println("Статус заказа 1 до отмены: " + order1.getStatus());
        bookstore.cancelOrder(1);
        System.out.println("Статус заказа 1 после отмены: " + order1.getStatus());
        System.out.println("Статус заказа 2 до изменения: " + order2.getStatus());
        bookstore.updateOrderStatus(2, Order.OrderStatus.COMPLETED);
        System.out.println("Статус заказа 2 после изменения: " + order2.getStatus());
        Book book4 = new Book("978-5-699-97388-5", "Три товарища", "Эрих Мария Ремарк", 520.0f);
        bookstore.addBookToInventory(book4);
        System.out.println("Статус книги после добавления: " + book4.getStatus());
        List<Book> order3Books = new ArrayList<>();
        Order order3 = bookstore.createOrder(3, order3Books);
        System.out.println("Статус заказа 3: " + order3.getStatus());
        System.out.println("Содержит отсутствующие книги: " + order3.containsOutOfStockBooks());
        Book requestedBook = new Book("978-5-271-46123-1", "Сто лет одиночества", "Габриэль Гарсиа Маркес", 610.0f);
        List<Book> tempBooks = new ArrayList<>();
        tempBooks.add(requestedBook);
        Order tempOrder = new Order(99, tempBooks);
        bookstore.createBookRequest(tempOrder, requestedBook);
    }
}


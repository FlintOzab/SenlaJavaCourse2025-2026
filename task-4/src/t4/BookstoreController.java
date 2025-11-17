package t4;
import java.util.Date;
import java.util.List;

public class BookstoreController {
	private final Bookstore Bookstore;
    
    public BookstoreController(Bookstore Bookstore) {
        this.Bookstore = Bookstore;
    }
    
    public void addBook(Book book) throws BookstoreException {
        Bookstore.addBookToInventory(book);
    }
    
    public void writeOffBook(String isbn) throws BookstoreException {
        Bookstore.writeOffBook(isbn);
    }
    
    public List<Book> getAllBooks() {
        return Bookstore.getBookInventory();
    }
    
    public Book findBookByIsbn(String isbn) {
        return Bookstore.findBookByIsbn(isbn);
    }
    
    public Order createOrder(int id, List<Book> books) throws BookstoreException {
        return Bookstore.createOrder(id, books);
    }
    
    public void cancelOrder(int orderId) throws BookstoreException {
        Bookstore.cancelOrder(orderId);
    }
    
    public void updateOrderStatus(int orderId, Order.OrderStatus status) throws BookstoreException {
        Bookstore.updateOrderStatus(orderId, status);
    }
    
    public Order findOrderById(int orderId) {
        return Bookstore.findOrderById(orderId);
    }
    
    public List<Order> getAllOrders() {
        return Bookstore.getAllOrders();
    }
    
    public void createBookRequest(Order order, Book book) {
        Bookstore.createBookRequest(order, book);
    }
    
    public List<Request> getActiveRequests() {
        return Bookstore.getActiveRequests();
    }
    
    public List<Book> getBooksSortedByTitle() {
        return Bookstore.getBooksSortedByTitle();
    }
    
    public List<Book> getBooksSortedByPublicationDate() {
        return Bookstore.getBooksSortedByPublicationDate();
    }
    
    public List<Book> getBooksSortedByPrice() {
        return Bookstore.getBooksSortedByPrice();
    }
    
    public List<Book> getBooksSortedByAvailability() {
        return Bookstore.getBooksSortedByAvailability();
    }
    
    public List<Order> getOrdersSortedByCompletionDate() {
        return Bookstore.getOrdersSortedByCompletionDate();
    }
    
    public List<Order> getOrdersSortedByTotalPrice() {
        return Bookstore.getOrdersSortedByTotalPrice();
    }
    
    public List<Order> getOrdersSortedByStatus() {
        return Bookstore.getOrdersSortedByStatus();
    }
    
    public List<Request> getRequestsSortedByCount() {
        return Bookstore.getRequestsSortedByCount();
    }
    
    public List<Request> getRequestsSortedByBookTitle() {
        return Bookstore.getRequestsSortedByBookTitle();
    }
    
    public List<Order> getCompletedOrdersInPeriod(Date startDate, Date endDate) {
        return Bookstore.getCompletedOrdersInPeriod(startDate, endDate);
    }
    
    public List<Order> getCompletedOrdersInPeriodSortedByPrice(Date startDate, Date endDate) {
        return Bookstore.getCompletedOrdersInPeriodSortedByPrice(startDate, endDate);
    }
    
    public long getTotalRevenueInPeriod(Date startDate, Date endDate) {
        return Bookstore.getTotalRevenueInPeriod(startDate, endDate);
    }
    
    public int getCompletedOrdersCountInPeriod(Date startDate, Date endDate) {
        return Bookstore.getCompletedOrdersCountInPeriod(startDate, endDate);
    }
    
    public List<Book> getOldBooks() {
        return Bookstore.getOldBooks();
    }
    
    public List<Book> getStaleBooksSortedByPrice() {
        return Bookstore.getStaleBooksSortedByPrice();
    }
    
    public String getOrderDetails(int orderId) {
        return Bookstore.getOrderDetails(orderId);
    }
    
    public String getBookDetails(String isbn) {
        return Bookstore.getBookDetails(isbn);
    }
    
    public void importBooksFromCSV(String filePath) throws BookstoreException {
        Bookstore.importBooksFromCSV(filePath);
    }
    
    public void exportBooksToCSV(String filePath) throws BookstoreException {
        Bookstore.exportBooksToCSV(filePath);
    }
    
    public void exportOrdersToCSV(String filePath) throws BookstoreException {
        Bookstore.exportOrdersToCSV(filePath);
    }
    
    public void exportRequestsToCSV(String filePath) throws BookstoreException {
        Bookstore.exportRequestsToCSV(filePath);
    }
}

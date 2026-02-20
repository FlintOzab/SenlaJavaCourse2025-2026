package dao;

/**
 * Constants for Data Access Object module.
 * Contains all string literals and magic numbers used in DAO classes.
 *
 * @author Bookstore Team
 * @version 1.0
 */
public final class DAOConstants {

    /** Parameter index for book ID in UPDATE query. */
    public static final int BOOK_UPDATE_ID_INDEX = 9;

    /** Parameter index for order ID in DELETE query. */
    public static final int ORDER_DELETE_ID_INDEX = 1;

    /** Parameter index for order status in UPDATE query. */
    public static final int ORDER_UPDATE_STATUS_INDEX = 1;

    /** Parameter index for order completion date in UPDATE query. */
    public static final int ORDER_UPDATE_COMPLETION_INDEX = 2;

    /** Parameter index for order ID in UPDATE query. */
    public static final int ORDER_UPDATE_ID_INDEX = 3;

    /** First parameter index in prepared statements. */
    public static final int PARAM_INDEX_1 = 1;

    /** Second parameter index in prepared statements. */
    public static final int PARAM_INDEX_2 = 2;

    /** Third parameter index in prepared statements. */
    public static final int PARAM_INDEX_3 = 3;

    /** Fourth parameter index in prepared statements. */
    public static final int PARAM_INDEX_4 = 4;

    /** Fifth parameter index in prepared statements. */
    public static final int PARAM_INDEX_5 = 5;

    /** Sixth parameter index in prepared statements. */
    public static final int PARAM_INDEX_6 = 6;

    /** Seventh parameter index in prepared statements. */
    public static final int PARAM_INDEX_7 = 7;

    /** Eighth parameter index in prepared statements. */
    public static final int PARAM_INDEX_8 = 8;

    /** Ninth parameter index in prepared statements. */
    public static final int PARAM_INDEX_9 = 9;

    /** Generated keys column index. */
    public static final int GENERATED_KEYS_INDEX = 1;

    /** Error message prefix for book not found by ID. */
    public static final String ERROR_BOOK_NOT_FOUND_BY_ID = "Ошибка при поиске книги по ID: ";

    /** Error message prefix for book not found by ISBN. */
    public static final String ERROR_BOOK_NOT_FOUND_BY_ISBN = "Ошибка при поиске книги по ISBN: ";

    /** Error message prefix for books not found by status. */
    public static final String ERROR_BOOKS_NOT_FOUND_BY_STATUS = "Ошибка при поиске книг по статусу: ";

    /** Error message for failed book save. */
    public static final String ERROR_BOOK_SAVE_FAILED = "Не удалось сохранить книгу: ";

    /** Error message for failed book update. */
    public static final String ERROR_BOOK_UPDATE_FAILED = "Книга не найдена для обновления: ";

    /** Error message for failed book delete. */
    public static final String ERROR_BOOK_DELETE_FAILED = "Книга не найдена для удаления: ";

    /** Error message for book update without ID. */
    public static final String ERROR_BOOK_UPDATE_NO_ID = "Нельзя обновить книгу без ID";

    /** Error message for missing generated ID. */
    public static final String ERROR_NO_GENERATED_ID = "Не удалось получить ID сохраненной книги";

    /** Error message prefix for order not found by ID. */
    public static final String ERROR_ORDER_NOT_FOUND_BY_ID = "Ошибка при поиске заказа по ID: ";

    /** Error message prefix for orders not found by status. */
    public static final String ERROR_ORDERS_NOT_FOUND_BY_STATUS = "Ошибка при поиске заказов по статусу: ";

    /** Error message for failed order save. */
    public static final String ERROR_ORDER_SAVE_FAILED = "Не удалось сохранить заказ";

    /** Error message for failed order update. */
    public static final String ERROR_ORDER_UPDATE_FAILED = "Заказ не найден для обновления: ";

    /** Error message for failed order delete. */
    public static final String ERROR_ORDER_DELETE_FAILED = "Заказ не найден для удаления: ";

    /** Error message for order update without ID. */
    public static final String ERROR_ORDER_UPDATE_NO_ID = "Нельзя обновить заказ без ID";

    /** Error message for failed order books loading. */
    public static final String ERROR_ORDER_BOOKS_LOAD_FAILED = "Ошибка при загрузке книг заказа: ";

    /** Error message for failed order books save. */
    public static final String ERROR_ORDER_BOOKS_SAVE_FAILED = "Ошибка при сохранении книг заказа: ";

    /** Error message prefix for request not found by ID. */
    public static final String ERROR_REQUEST_NOT_FOUND_BY_ID = "Ошибка при поиске запроса по ID: ";

    /** Error message prefix for requests not found by order. */
    public static final String ERROR_REQUESTS_NOT_FOUND_BY_ORDER = "Ошибка при поиске запросов по order_id: ";

    /** Error message prefix for requests not found by book. */
    public static final String ERROR_REQUESTS_NOT_FOUND_BY_BOOK = "Ошибка при поиске запросов по book_id: ";

    /** Error message for failed request save. */
    public static final String ERROR_REQUEST_SAVE_FAILED = "Не удалось сохранить запрос: ";

    /** Error message for failed request update. */
    public static final String ERROR_REQUEST_UPDATE_FAILED = "Запрос не найден для обновления: ";

    /** Error message for failed request delete. */
    public static final String ERROR_REQUEST_DELETE_FAILED = "Запрос не найден для удаления: ";

    /** Error message for request update without ID. */
    public static final String ERROR_REQUEST_UPDATE_NO_ID = "Нельзя обновить запрос без ID";

    /** Error message for failed active requests fetch. */
    public static final String ERROR_ACTIVE_REQUESTS_FAILED = "Ошибка при получении активных запросов";

    /** Error message for order not found during request mapping. */
    public static final String ERROR_ORDER_NOT_FOUND = "Заказ не найден: ";

    /** Error message for book not found during request mapping. */
    public static final String ERROR_BOOK_NOT_FOUND = "Книга не найдена: ";

    /** Error message for failed book fetch. */
    public static final String ERROR_BOOK_FETCH_FAILED = "Ошибка при получении всех книг";

    /** Error message for failed order fetch. */
    public static final String ERROR_ORDER_FETCH_FAILED = "Ошибка при получении всех заказов";

    /** Error message for failed request fetch. */
    public static final String ERROR_REQUEST_FETCH_FAILED = "Ошибка при получении всех запросов";

    /**
     * Private constructor to prevent instantiation.
     */
    private DAOConstants() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
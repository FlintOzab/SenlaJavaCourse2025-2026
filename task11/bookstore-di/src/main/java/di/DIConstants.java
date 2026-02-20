package di;

/**
 * Constants for dependency injection module.
 * Contains all string literals used in the DI module.
 *
 * @author Bookstore Team
 * @version 1.0
 */
public final class DIConstants {

    /** Error message prefix for component not found by class. */
    public static final String ERROR_COMPONENT_NOT_FOUND = "Компонент не найден: ";

    /** Error message prefix for component not found by name. */
    public static final String ERROR_COMPONENT_NOT_FOUND_BY_NAME = "Компонент не найден: ";

    /** Error message prefix for component creation failure. */
    public static final String ERROR_COMPONENT_CREATION = "Ошибка создания компонента ";

    /** Error message prefix for dependency not found. */
    public static final String ERROR_DEPENDENCY_NOT_FOUND = "Не удалось найти зависимость для поля ";

    /** Suffix for field type in error message. */
    public static final String ERROR_FIELD_TYPE_SUFFIX = " типа ";

    /** Error message for config file not found. */
    public static final String ERROR_CONFIG_NOT_FOUND = "Файл конфигурации не найден: ";

    /** Component annotation name default (empty). */
    public static final String COMPONENT_NAME_DEFAULT = "";

    /** Inject annotation value default (empty). */
    public static final String INJECT_VALUE_DEFAULT = "";

    /** Config property file name default. */
    public static final String CONFIG_FILE_NAME_DEFAULT = "bookstore.properties";

    /** Config property name default (empty). */
    public static final String CONFIG_PROPERTY_NAME_DEFAULT = "";

    /** Config property type default - AUTO. */
    public static final String CONFIG_PROPERTY_TYPE_DEFAULT = "AUTO";

    /** Error message for utility class instantiation. */
    public static final String ERROR_UTILITY_CLASS = "Utility class should not be instantiated";

    /**
     * Private constructor to prevent instantiation.
     */
    private DIConstants() {
        throw new AssertionError(ERROR_UTILITY_CLASS);
    }
}

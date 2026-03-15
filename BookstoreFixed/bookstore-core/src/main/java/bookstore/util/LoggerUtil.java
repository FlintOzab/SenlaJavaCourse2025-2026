package bookstore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for logging operations.
 * Provides convenience methods for common logging patterns.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public final class LoggerUtil {
    
    /** Maximum length for parameter list formatting. */
    private static final int MAX_PARAM_LENGTH = 100;
    
    /**
     * Private constructor to prevent instantiation.
     */
    private LoggerUtil() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * Gets a logger for the specified class.
     * 
     * @param clazz the class to get logger for
     * @return the logger instance
     */
    public static Logger getLogger(final Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    /**
     * Logs the start of a command execution.
     * 
     * @param logger the logger to use
     * @param commandName the name of the command
     * @param params optional parameters
     */
    public static void logCommandStart(final Logger logger, 
                                       final String commandName, 
                                       final Object... params) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Начало выполнения команды: ").append(commandName);
        
        if (params != null && params.length > 0) {
            logMessage.append(" с параметрами: ");
            for (Object param : params) {
                if (param != null) {
                    String paramStr = param.toString();
                    if (paramStr.length() > MAX_PARAM_LENGTH) {
                        paramStr = paramStr.substring(0, MAX_PARAM_LENGTH) + "...";
                    }
                    logMessage.append(paramStr).append(", ");
                } else {
                    logMessage.append("null").append(", ");
                }
            }
            logMessage.setLength(logMessage.length() - 2);
        }
        
        logger.info(logMessage.toString());
    }
    
    /**
     * Logs successful completion of a command.
     * 
     * @param logger the logger to use
     * @param commandName the name of the command
     * @param result the result of the command
     */
    public static void logCommandSuccess(final Logger logger, 
                                         final String commandName, 
                                         final Object result) {
        if (result != null) {
            logger.info("Команда {} успешно выполнена. Результат: {}", commandName, result);
        } else {
            logger.info("Команда {} успешно выполнена.", commandName);
        }
    }
    
    /**
     * Logs an error during command execution.
     * 
     * @param logger the logger to use
     * @param commandName the name of the command
     * @param error the exception that occurred
     */
    public static void logCommandError(final Logger logger, 
                                       final String commandName, 
                                       final Exception error) {
        logger.error("Ошибка выполнения команды {}: {}", commandName, error.getMessage(), error);
    }
}
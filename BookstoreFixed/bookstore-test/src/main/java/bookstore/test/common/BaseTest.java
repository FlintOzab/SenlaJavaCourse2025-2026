package bookstore.test.common;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Base test class with common setup and teardown.
 * All test classes should extend this class.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseTest {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * Setup before each test.
     */
    @BeforeEach
    public void setUp() {
        logger.info("Starting test: {}", getClass().getSimpleName());
    }
    
    /**
     * Cleanup after each test.
     */
    @AfterEach
    public void tearDown() {
        logger.info("Finished test: {}", getClass().getSimpleName());
    }
    
    /**
     * Log a debug message with test details.
     * 
     * @param message the message to log
     * @param params optional parameters
     */
    protected void logDebug(String message, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, params);
        }
    }
    
    /**
     * Log an info message with test details.
     * 
     * @param message the message to log
     * @param params optional parameters
     */
    protected void logInfo(String message, Object... params) {
        logger.info(message, params);
    }
    
    /**
     * Creates a temporary file for testing.
     * 
     * @param prefix file prefix
     * @param suffix file suffix
     * @return path to temporary file
     * @throws IOException if file creation fails
     */
    protected Path createTempFile(String prefix, String suffix) throws IOException {
        return Files.createTempFile(prefix, suffix);
    }
}
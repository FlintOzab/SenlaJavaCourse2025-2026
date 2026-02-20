package di;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class for loading and managing property files.
 * Provides methods to load properties from files and access them.
 *
 * @author Bookstore Team
 * @version 1.0
 */
public class PropertyLoader {

    /** Map of loaded properties. */
    private final Map<String, String> properties;

    /**
     * Creates a new empty property loader.
     */
    public PropertyLoader() {
        this.properties = new HashMap<>();
    }

    /**
     * Loads properties from a file in the classpath.
     *
     * @param fileName the name of the property file
     * @throws IOException if the file cannot be loaded
     */
    public void loadFromFile(final String fileName) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IOException(DIConstants.ERROR_CONFIG_NOT_FOUND + fileName);
            }
            Properties props = new Properties();
            props.load(input);

            for (String key : props.stringPropertyNames()) {
                properties.put(key, props.getProperty(key));
            }
        }
    }

    /**
     * Sets a property value.
     *
     * @param key the property key
     * @param value the property value
     */
    public void setProperty(final String key, final String value) {
        properties.put(key, value);
    }

    /**
     * Gets a property value by key.
     *
     * @param key the property key
     * @return the property value, or null if not found
     */
    public String getProperty(final String key) {
        return properties.get(key);
    }

    /**
     * Returns all loaded properties.
     *
     * @return a map of all properties
     */
    public Map<String, String> getAllProperties() {
        return new HashMap<>(properties);
    }
}

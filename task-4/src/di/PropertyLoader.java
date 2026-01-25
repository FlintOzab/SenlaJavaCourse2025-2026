package di;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyLoader {
    private final Map<String, String> properties = new HashMap<>();
    
    public PropertyLoader() {
    }
    
    public void loadFromFile(String fileName) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IOException("Файл конфигурации не найден: " + fileName);
            }
            Properties props = new Properties();
            props.load(input);
            
            for (String key : props.stringPropertyNames()) {
                properties.put(key, props.getProperty(key));
            }
        }
    }
    
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
    
    public String getProperty(String key) {
        return properties.get(key);
    }
    
    public Map<String, String> getAllProperties() {
        return new HashMap<>(properties);
    }
}
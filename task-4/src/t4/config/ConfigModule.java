package t4.config;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import di.annotation.ConfigProperty;
import t4.exception.ConfigurationException;


public class ConfigModule {
    
    private static final Map<String, Properties> loadedConfigs = new HashMap<>();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
 
    public static void configure(Object configObject) throws ConfigurationException {
        configure(configObject, new HashSet<>());
    }

    public static void configureAll(Collection<?> configObjects) throws ConfigurationException {
        if (configObjects == null) return;
        
        Set<Object> visited = new HashSet<>();
        for (Object configObject : configObjects) {
            configure(configObject, visited);
        }
    }
    
    private static void configure(Object configObject, Set<Object> visited) 
            throws ConfigurationException {
        
        if (configObject == null || visited.contains(configObject)) {
            return;
        }
        
        visited.add(configObject);
        Class<?> clazz = configObject.getClass();
        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                try {
                    configureField(configObject, field);
                } catch (Exception e) {
                    throw new ConfigurationException(
                        "Ошибка конфигурации поля " + field.getName() + 
                        " в классе " + clazz.getName(), e
                    );
                }
            }
        }
        
        for (Field field : getAllFields(clazz)) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(configObject);
                
                if (fieldValue != null && !isPrimitiveOrWrapper(fieldValue.getClass())) {
                    if (!fieldValue.getClass().isArray() && !Collection.class.isAssignableFrom(fieldValue.getClass())) {
                        configure(fieldValue, visited);
                    }
                }
            } catch (Exception e) {
            }
        }
    }
    
    public static void configureContainer(Object container) throws ConfigurationException {
        try {
            if (container instanceof Iterable) {
                for (Object instance : (Iterable<?>) container) {
                    configure(instance);
                }
            } else {
                Class<?> containerClass = container.getClass();
                try {
                    java.lang.reflect.Method method = containerClass.getMethod("getAllInstances");
                    Set<?> instances = (Set<?>) method.invoke(container);
                    configureAll(instances);
                } catch (NoSuchMethodException e) {
                    configure(container);
                }
            }
        } catch (Exception e) {
            throw new ConfigurationException("Ошибка конфигурации контейнера", e);
        }
    }
    
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
    
    private static void configureField(Object configObject, Field field) 
            throws IllegalAccessException, IOException, ParseException {
        
        ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
        String configFileName = annotation.configFileName();
        String propertyName = getPropertyName(annotation, field);
        String propertyValue = getPropertyValue(configFileName, propertyName);
        
        if (propertyValue != null) {
            field.setAccessible(true);
            Object convertedValue = convertValue(propertyValue, field.getType(), 
                annotation.type(), field);
            field.set(configObject, convertedValue);
        }
    }
    
    private static String getPropertyName(ConfigProperty annotation, Field field) {
        if (!annotation.propertyName().isEmpty()) {
            return annotation.propertyName();
        }
        
        String className = field.getDeclaringClass().getSimpleName();
        String fieldName = field.getName();
        return className + "." + fieldName;
    }
    
    private static String getPropertyValue(String configFileName, String propertyName) 
            throws IOException {
        
        Properties properties = getProperties(configFileName);
        return properties.getProperty(propertyName);
    }
    
    private static Properties getProperties(String configFileName) throws IOException {
        Properties properties = loadedConfigs.get(configFileName);
        if (properties == null) {
            properties = loadProperties(configFileName);
            loadedConfigs.put(configFileName, properties);
        }
        return properties;
    }
    
    private static Properties loadProperties(String configFileName) throws IOException {
        Properties properties = new Properties();
        
        try (InputStream input = ConfigModule.class.getClassLoader()
                .getResourceAsStream(configFileName)) {
            
            if (input != null) {
                properties.load(input);
            } else {
                try (InputStream fsInput = new java.io.FileInputStream(configFileName)) {
                    properties.load(fsInput);
                } catch (IOException e) {
                    System.out.println("Конфигурационный файл не найден: " + configFileName);
                }
            }
        }
        
        return properties;
    }
    
    private static Object convertValue(String value, Class<?> targetType, 
                                      ConfigProperty.PropertyType annotationType,
                                      Field field) throws ParseException {
        
        if (annotationType != ConfigProperty.PropertyType.AUTO) {
            return convertByAnnotationType(value, annotationType, field);
        }
        
        return convertByFieldType(value, targetType, field);
    }
    
    private static Object convertByAnnotationType(String value, 
                                                 ConfigProperty.PropertyType type,
                                                 Field field) throws ParseException {
        switch (type) {
            case STRING:
                return value;
            case INTEGER:
                return Integer.parseInt(value);
            case LONG:
                return Long.parseLong(value);
            case BOOLEAN:
                return Boolean.parseBoolean(value);
            case DOUBLE:
                return Double.parseDouble(value);
            case DATE:
                return DATE_FORMAT.parse(value);
            case ARRAY:
                return convertToArray(value, String.class);
            case LIST:
                return convertToList(value, String.class);
            case SET:
                return convertToSet(value, String.class);
            default:
                return value;
        }
    }
    
    private static Object convertByFieldType(String value, Class<?> targetType, Field field) 
            throws ParseException {
        
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == Date.class) {
            return DATE_FORMAT.parse(value);
        } else if (targetType.isArray()) {
            Class<?> componentType = targetType.getComponentType();
            return convertToArray(value, componentType);
        } else if (List.class.isAssignableFrom(targetType)) {
            Class<?> elementType = getGenericType(field, 0, String.class);
            return convertToList(value, elementType);
        } else if (Set.class.isAssignableFrom(targetType)) {
            Class<?> elementType = getGenericType(field, 0, String.class);
            return convertToSet(value, elementType);
        }
        
        return value;
    }
    
    private static Object convertToArray(String value, Class<?> elementType) {
        String[] parts = splitValue(value);
        Object array = Array.newInstance(elementType, parts.length);
        
        for (int i = 0; i < parts.length; i++) {
            Array.set(array, i, convertSimpleValue(parts[i], elementType));
        }
        
        return array;
    }
    
    private static List<?> convertToList(String value, Class<?> elementType) {
        String[] parts = splitValue(value);
        List<Object> list = new ArrayList<>();
        
        for (String part : parts) {
            list.add(convertSimpleValue(part, elementType));
        }
        
        return list;
    }
    
    private static Set<?> convertToSet(String value, Class<?> elementType) {
        String[] parts = splitValue(value);
        Set<Object> set = new HashSet<>();
        
        for (String part : parts) {
            set.add(convertSimpleValue(part, elementType));
        }
        
        return set;
    }
    
    private static String[] splitValue(String value) {
        return value.split("\\s*,\\s*");
    }
    
    private static Object convertSimpleValue(String value, Class<?> type) {
        try {
            if (type == String.class) {
                return value;
            } else if (type == Integer.class || type == int.class) {
                return Integer.parseInt(value);
            } else if (type == Long.class || type == long.class) {
                return Long.parseLong(value);
            } else if (type == Boolean.class || type == boolean.class) {
                return Boolean.parseBoolean(value);
            } else if (type == Double.class || type == double.class) {
                return Double.parseDouble(value);
            } else if (type == Date.class) {
                return DATE_FORMAT.parse(value);
            }
        } catch (Exception e) {
            throw new ConfigurationException("Не удалось преобразовать значение '" + 
                value + "' в тип " + type.getName(), e);
        }
        
        return value;
    }
    
    private static Class<?> getGenericType(Field field, int index, Class<?> defaultType) {
        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) field.getGenericType();
            if (paramType.getActualTypeArguments().length > index) {
                return (Class<?>) paramType.getActualTypeArguments()[index];
            }
        }
        return defaultType;
    }
    
    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || 
               clazz == Boolean.class ||
               clazz == Character.class ||
               clazz == Byte.class ||
               clazz == Short.class ||
               clazz == Integer.class ||
               clazz == Long.class ||
               clazz == Float.class ||
               clazz == Double.class ||
               clazz == String.class ||
               clazz == Date.class;
    }
    
    public static List<Field> findConfigPropertyFields(Object object) {
        List<Field> configFields = new ArrayList<>();
        if (object == null) return configFields;
        
        Class<?> clazz = object.getClass();
        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                configFields.add(field);
            }
        }
        
        return configFields;
    }
    
    public static boolean hasConfigProperties(Object object) {
        if (object == null) return false;
        
        Class<?> clazz = object.getClass();
        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                return true;
            }
        }
        
        return false;
    }
}

package config;

import exception.ConfigurationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Module for configuring objects with ConfigProperty annotations.
 * Loads properties from files and injects them into annotated fields.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public final class ConfigModule {
    
    /** Cache of loaded configuration files. */
    private static final Map<String, Properties> LOADED_CONFIGS = new HashMap<>();
    
    /** Date format for property values. */
    private static final SimpleDateFormat DATE_FORMAT = 
        new SimpleDateFormat("dd.MM.yyyy");
    
    /** Separator for list/array values. */
    private static final String VALUE_SEPARATOR = "\\s*,\\s*";
    
    /**
     * Private constructor to prevent instantiation.
     */
    private ConfigModule() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * Configures an object by injecting property values.
     * 
     * @param configObject the object to configure
     * @throws ConfigurationException if configuration fails
     */
    public static void configure(final Object configObject) 
            throws ConfigurationException {
        configure(configObject, new HashSet<>());
    }
    
    /**
     * Configures a collection of objects.
     * 
     * @param configObjects the collection of objects to configure
     * @throws ConfigurationException if configuration fails
     */
    public static void configureAll(final Collection<?> configObjects) 
            throws ConfigurationException {
        if (configObjects == null) {
            return;
        }
        
        Set<Object> visited = new HashSet<>();
        for (Object configObject : configObjects) {
            configure(configObject, visited);
        }
    }
    
    /**
     * Configures an object with cycle detection.
     * 
     * @param configObject the object to configure
     * @param visited set of already visited objects
     * @throws ConfigurationException if configuration fails
     */
    private static void configure(final Object configObject, 
                                   final Set<Object> visited) 
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
                        "Ошибка конфигурации поля " + field.getName() 
                        + " в классе " + clazz.getName(), e
                    );
                }
            }
        }
        
        for (Field field : getAllFields(clazz)) {
            configureNestedObject(configObject, field, visited);
        }
    }
    
    /**
     * Configures a nested object if it's not a primitive.
     * 
     * @param configObject the parent object
     * @param field the field to check
     * @param visited set of visited objects
     */
    private static void configureNestedObject(final Object configObject,
                                               final Field field,
                                               final Set<Object> visited) {
        try {
            field.setAccessible(true);
            Object fieldValue = field.get(configObject);
            
            if (fieldValue != null && !isPrimitiveOrWrapper(fieldValue.getClass())) {
                if (!fieldValue.getClass().isArray() 
                        && !Collection.class.isAssignableFrom(fieldValue.getClass())) {
                    configure(fieldValue, visited);
                }
            }
        } catch (IllegalAccessException e) {
            // Ignore, continue with next field
        }
    }
    
    /**
     * Configures a container object.
     * 
     * @param container the container to configure
     * @throws ConfigurationException if configuration fails
     */
    public static void configureContainer(final Object container) 
            throws ConfigurationException {
        try {
            if (container instanceof Iterable) {
                for (Object instance : (Iterable<?>) container) {
                    configure(instance);
                }
            } else {
                Class<?> containerClass = container.getClass();
                try {
                    java.lang.reflect.Method method = 
                        containerClass.getMethod("getAllInstances");
                    Set<?> instances = (Set<?>) method.invoke(container);
                    configureAll(instances);
                } catch (NoSuchMethodException e) {
                    configure(container);
                }
            }
        } catch (Exception e) {
            throw new ConfigurationException(
                "Ошибка конфигурации контейнера", e);
        }
    }
    
    /**
     * Gets all fields from class hierarchy.
     * 
     * @param clazz the class to get fields from
     * @return list of all fields
     */
    private static List<Field> getAllFields(final Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }
    
    /**
     * Configures a single field with property value.
     * 
     * @param configObject the object containing the field
     * @param field the field to configure
     * @throws IllegalAccessException if field access fails
     * @throws IOException if property loading fails
     * @throws ParseException if value parsing fails
     */
    private static void configureField(final Object configObject, 
                                        final Field field) 
            throws IllegalAccessException, IOException, ParseException {
        
        ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
        String configFileName = annotation.configFileName();
        String propertyName = getPropertyName(annotation, field);
        String propertyValue = getPropertyValue(configFileName, propertyName);
        
        if (propertyValue != null) {
            field.setAccessible(true);
            Object convertedValue = convertValue(propertyValue, 
                field.getType(), annotation.type(), field);
            field.set(configObject, convertedValue);
        }
    }
    
    /**
     * Gets the property name from annotation or constructs it.
     * 
     * @param annotation the annotation
     * @param field the field
     * @return the property name
     */
    private static String getPropertyName(final ConfigProperty annotation,
                                           final Field field) {
        if (!annotation.propertyName().isEmpty()) {
            return annotation.propertyName();
        }
        
        String className = field.getDeclaringClass().getSimpleName();
        String fieldName = field.getName();
        return className + "." + fieldName;
    }
    
    /**
     * Gets property value from configuration file.
     * 
     * @param configFileName the configuration file name
     * @param propertyName the property name
     * @return the property value or null
     * @throws IOException if file loading fails
     */
    private static String getPropertyValue(final String configFileName,
                                            final String propertyName) 
            throws IOException {
        
        Properties properties = getProperties(configFileName);
        return properties.getProperty(propertyName);
    }
    
    /**
     * Gets or loads properties from file.
     * 
     * @param configFileName the configuration file name
     * @return the properties
     * @throws IOException if file loading fails
     */
    private static Properties getProperties(final String configFileName) 
            throws IOException {
        Properties properties = LOADED_CONFIGS.get(configFileName);
        if (properties == null) {
            properties = loadProperties(configFileName);
            LOADED_CONFIGS.put(configFileName, properties);
        }
        return properties;
    }
    
    /**
     * Loads properties from file.
     * 
     * @param configFileName the configuration file name
     * @return the loaded properties
     * @throws IOException if file loading fails
     */
    private static Properties loadProperties(final String configFileName) 
            throws IOException {
        Properties properties = new Properties();
        
        try (InputStream input = ConfigModule.class.getClassLoader()
                .getResourceAsStream(configFileName)) {
            
            if (input != null) {
                properties.load(input);
            } else {
                try (InputStream fsInput = new FileInputStream(configFileName)) {
                    properties.load(fsInput);
                } catch (IOException e) {
                    System.out.println("Конфигурационный файл не найден: " 
                        + configFileName);
                }
            }
        }
        
        return properties;
    }
    
    /**
     * Converts string value to target type.
     * 
     * @param value the string value
     * @param targetType the target type
     * @param annotationType the annotation type
     * @param field the field
     * @return the converted value
     * @throws ParseException if parsing fails
     */
    private static Object convertValue(final String value,
                                        final Class<?> targetType,
                                        final ConfigProperty.PropertyType annotationType,
                                        final Field field) throws ParseException {
        
        if (annotationType != ConfigProperty.PropertyType.AUTO) {
            return convertByAnnotationType(value, annotationType, field);
        }
        
        return convertByFieldType(value, targetType, field);
    }
    
    /**
     * Converts value by annotation type.
     * 
     * @param value the string value
     * @param type the annotation type
     * @param field the field
     * @return the converted value
     * @throws ParseException if parsing fails
     */
    private static Object convertByAnnotationType(final String value,
                                                   final ConfigProperty.PropertyType type,
                                                   final Field field) throws ParseException {
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
    
    /**
     * Converts value by field type.
     * 
     * @param value the string value
     * @param targetType the target type
     * @param field the field
     * @return the converted value
     * @throws ParseException if parsing fails
     */
    private static Object convertByFieldType(final String value,
                                              final Class<?> targetType,
                                              final Field field) throws ParseException {
        
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
    
    /**
     * Converts string to array.
     * 
     * @param value the string value
     * @param elementType the array element type
     * @return the array
     */
    private static Object convertToArray(final String value,
                                          final Class<?> elementType) {
        String[] parts = splitValue(value);
        Object array = Array.newInstance(elementType, parts.length);
        
        for (int i = 0; i < parts.length; i++) {
            Array.set(array, i, convertSimpleValue(parts[i], elementType));
        }
        
        return array;
    }
    
    /**
     * Converts string to list.
     * 
     * @param value the string value
     * @param elementType the list element type
     * @return the list
     */
    private static List<?> convertToList(final String value,
                                          final Class<?> elementType) {
        String[] parts = splitValue(value);
        List<Object> list = new ArrayList<>();
        
        for (String part : parts) {
            list.add(convertSimpleValue(part, elementType));
        }
        
        return list;
    }
    
    /**
     * Converts string to set.
     * 
     * @param value the string value
     * @param elementType the set element type
     * @return the set
     */
    private static Set<?> convertToSet(final String value,
                                        final Class<?> elementType) {
        String[] parts = splitValue(value);
        Set<Object> set = new HashSet<>();
        
        for (String part : parts) {
            set.add(convertSimpleValue(part, elementType));
        }
        
        return set;
    }
    
    /**
     * Splits a comma-separated value.
     * 
     * @param value the value to split
     * @return array of parts
     */
    private static String[] splitValue(final String value) {
        return value.split(VALUE_SEPARATOR);
    }
    
    /**
     * Converts a simple value to the target type.
     * 
     * @param value the string value
     * @param type the target type
     * @return the converted value
     */
    private static Object convertSimpleValue(final String value,
                                              final Class<?> type) {
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
            throw new ConfigurationException(
                "Не удалось преобразовать значение '" + value 
                + "' в тип " + type.getName(), e);
        }
        
        return value;
    }
    
    /**
     * Gets generic type parameter of a field.
     * 
     * @param field the field
     * @param index the parameter index
     * @param defaultType the default type
     * @return the generic type
     */
    private static Class<?> getGenericType(final Field field,
                                            final int index,
                                            final Class<?> defaultType) {
        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType paramType = 
                (ParameterizedType) field.getGenericType();
            if (paramType.getActualTypeArguments().length > index) {
                return (Class<?>) paramType.getActualTypeArguments()[index];
            }
        }
        return defaultType;
    }
    
    /**
     * Checks if a class is a primitive or wrapper type.
     * 
     * @param clazz the class to check
     * @return true if primitive or wrapper
     */
    private static boolean isPrimitiveOrWrapper(final Class<?> clazz) {
        return clazz.isPrimitive() 
            || clazz == Boolean.class
            || clazz == Character.class
            || clazz == Byte.class
            || clazz == Short.class
            || clazz == Integer.class
            || clazz == Long.class
            || clazz == Float.class
            || clazz == Double.class
            || clazz == String.class
            || clazz == Date.class;
    }
    
    /**
     * Finds all fields with ConfigProperty annotation.
     * 
     * @param object the object to inspect
     * @return list of config property fields
     */
    public static List<Field> findConfigPropertyFields(final Object object) {
        List<Field> configFields = new ArrayList<>();
        if (object == null) {
            return configFields;
        }
        
        Class<?> clazz = object.getClass();
        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                configFields.add(field);
            }
        }
        
        return configFields;
    }
    
    /**
     * Checks if an object has any config properties.
     * 
     * @param object the object to check
     * @return true if object has config properties
     */
    public static boolean hasConfigProperties(final Object object) {
        if (object == null) {
            return false;
        }
        
        Class<?> clazz = object.getClass();
        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                return true;
            }
        }
        
        return false;
    }
}
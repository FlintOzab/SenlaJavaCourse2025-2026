package t4;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyInjector {
    
    private static final Map<String, Object> components = new ConcurrentHashMap<>();
    private static final Map<String, Object> configProperties = new ConcurrentHashMap<>();
    private static boolean initialized = false;
    
    private static final Map<Class<?>, Class<?>> interfaceImplementations = new ConcurrentHashMap<>();
    
    static {
        registerInterfaceImplementation(Display.class, ConsoleDisplay.class);
        registerInterfaceImplementation(Input.class, ConsoleInput.class);
    }
    
    private static void registerInterfaceImplementation(Class<?> interfaceType, Class<?> implementationType) {
        interfaceImplementations.put(interfaceType, implementationType);
    }
    
    private DependencyInjector() {}
    
    public static void initialize(String... basePackages) {
        if (initialized) {
            System.out.println("DI контейнер уже инициализирован");
            return;
        }
        
        System.out.println("Инициализация DI контейнера");
        
        loadDefaultConfiguration();
        
        for (String basePackage : basePackages) {
            scanAndRegisterComponents(basePackage);
        }
        
        System.out.println("Найдено компонентов: " + components.size());
        
        loadConfigurationFromFiles();
        
        injectDependencies();
        
        initialized = true;
        System.out.println("DI контейнер успешно инициализирован");
    }
    
    public static <T> T getComponent(Class<T> type) {
        return getComponent(type, null);
    }
    
    public static <T> T getComponent(Class<T> type, String name) {
        String key = (name != null && !name.isEmpty()) ? name : type.getName();
        Object component = components.get(key);
        
        if (component != null && type.isInstance(component)) {
            return type.cast(component);
        }
        
        if (type.isInterface()) {
            Class<?> implementation = interfaceImplementations.get(type);
            if (implementation != null) {
                return getComponent((Class<T>) implementation, name);
            }
        }
        
        return createComponent(type, name);
    }
    
    private static void scanAndRegisterComponents(String... basePackages) {
        for (String basePackage : basePackages) {
            try {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                String path = basePackage.replace('.', '/');
                
                Enumeration<java.net.URL> resources = classLoader.getResources(path);
                while (resources.hasMoreElements()) {
                    java.net.URL resource = resources.nextElement();
                    if (resource.getProtocol().equals("file")) {
                        findAndRegisterComponents(basePackage, 
                            new java.io.File(resource.getFile()), basePackage);
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка сканирования пакета " + basePackage + ": " + e.getMessage());
            }
        }
    }
    
    private static void findAndRegisterComponents(String basePackage, 
                                                  java.io.File directory, 
                                                  String currentPackage) {
        if (!directory.exists()) return;
        
        java.io.File[] files = directory.listFiles();
        if (files == null) return;
        
        for (java.io.File file : files) {
            if (file.isDirectory()) {
                findAndRegisterComponents(basePackage, file, 
                    currentPackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = currentPackage + "." + 
                    file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(Component.class)) {
                        registerComponent(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Класс не найден: " + className);
                }
            }
        }
    }
    
    private static void registerComponent(Class<?> clazz) {
        Component componentAnnotation = clazz.getAnnotation(Component.class);
        String componentName = componentAnnotation.name().isEmpty() ? 
            clazz.getName() : componentAnnotation.name();
        
        if (!components.containsKey(componentName)) {
            System.out.println("Регистрация компонента: " + clazz.getName());
            Object instance = createComponent(clazz, componentName);
            if (instance != null) {
                components.put(componentName, instance);
                components.put(clazz.getName(), instance);
                
                for (Class<?> iface : clazz.getInterfaces()) {
                    if (!interfaceImplementations.containsKey(iface)) {
                        interfaceImplementations.put(iface, clazz);
                        System.out.println("  Регистрация реализации интерфейса: " + 
                            iface.getName() + " -> " + clazz.getName());
                    }
                }
            }
        }
    }
    
    private static <T> T createComponent(Class<T> type, String name) {
        try {
            System.out.println("Создание компонента: " + type.getName());
            
            Constructor<?>[] constructors = type.getConstructors();
            Constructor<?> injectConstructor = null;
            
            for (Constructor<?> constructor : constructors) {
                if (constructor.isAnnotationPresent(Inject.class)) {
                    injectConstructor = constructor;
                    System.out.println("  Найден конструктор с @Inject");
                    break;
                }
            }
            
            T instance;
            if (injectConstructor != null) {
                Class<?>[] paramTypes = injectConstructor.getParameterTypes();
                Object[] params = new Object[paramTypes.length];
                
                System.out.println("  Параметры конструктора: " + params.length);
                for (int i = 0; i < paramTypes.length; i++) {
                    System.out.println("    Параметр " + i + ": " + paramTypes[i].getName());
                    params[i] = getComponent(paramTypes[i]);
                    if (params[i] == null) {
                        System.err.println("    Не удалось получить компонент для типа: " + paramTypes[i].getName());
                    }
                }
                
                instance = type.cast(injectConstructor.newInstance(params));
            } else {
                System.out.println("  Использование конструктора по умолчанию");
                instance = type.getDeclaredConstructor().newInstance();
            }
            
            injectFieldDependencies(instance);
            applyConfiguration(instance);
            
            System.out.println("  Компонент успешно создан: " + type.getName());
            return instance;
            
        } catch (Exception e) {
            System.err.println("Ошибка создания компонента " + type.getName() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public static void injectFieldDependencies(Object instance) throws IllegalAccessException {
        if (instance == null) return;
        
        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Inject injectAnnotation = field.getAnnotation(Inject.class);
                String dependencyName = injectAnnotation.value();
                
                Object dependency;
                if (!dependencyName.isEmpty()) {
                    dependency = getComponent(field.getType(), dependencyName);
                } else {
                    dependency = getComponent(field.getType());
                }
                
                if (dependency != null) {
                    field.set(instance, dependency);
                    System.out.println("  Внедрена зависимость в поле " + field.getName() + 
                                     " класса " + clazz.getName());
                } else {
                    System.err.println("  Не удалось внедрить зависимость для поля " + 
                                     field.getName() + " в классе " + clazz.getName() +
                                     " (тип: " + field.getType().getName() + ")");
                }
            }
        }
    }
    
    private static void loadDefaultConfiguration() {
        configProperties.put("stale.months.threshold", "6");
        configProperties.put("auto.fulfill.requests", "true");
        configProperties.put("default.book.price", "100");
        configProperties.put("export.directory", "./exports/");
    }
    
    private static void loadConfigurationFromFiles() {
        Set<String> configFiles = new HashSet<>();
        
        for (Object component : components.values()) {
            for (Field field : component.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigProperty.class)) {
                    ConfigProperty configAnnotation = field.getAnnotation(ConfigProperty.class);
                    configFiles.add(configAnnotation.configFileName());
                }
            }
        }
        
        configFiles.add("bookstore.properties");
        
        for (String configFile : configFiles) {
            try (InputStream input = new java.io.FileInputStream(configFile)) {
                Properties props = new Properties();
                props.load(input);
                
                for (String key : props.stringPropertyNames()) {
                    configProperties.put(key, props.getProperty(key));
                }
                
                System.out.println("Конфигурация загружена из файла: " + configFile);
            } catch (IOException e) {
                System.out.println("Конфигурационный файл не найден: " + configFile + ", используются значения по умолчанию");
            }
        }
    }
    
    public static void applyConfiguration(Object instance) {
        if (instance == null) return;
        
        try {
            Class<?> clazz = instance.getClass();
            boolean configApplied = false;
            
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigProperty.class)) {
                    ConfigProperty configAnnotation = field.getAnnotation(ConfigProperty.class);
                    String propertyName = getPropertyName(configAnnotation, field);
                    String propertyValue = (String) configProperties.get(propertyName);
                    
                    if (propertyValue != null) {
                        field.setAccessible(true);
                        Object convertedValue = convertValue(propertyValue, field.getType(), 
                            configAnnotation.type(), field);
                        field.set(instance, convertedValue);
                        configApplied = true;
                        System.out.println("  Применена конфигурация к полю " + field.getName() + 
                                         ": " + propertyName + " = " + propertyValue);
                    }
                }
            }
            
            if (configApplied) {
                System.out.println("  Конфигурация применена к " + clazz.getName());
            }
        } catch (Exception e) {
            System.err.println("Ошибка применения конфигурации к " + 
                instance.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    public static void applyConfigurationTo(Object instance) {
        applyConfiguration(instance);
    }
    
    private static String getPropertyName(ConfigProperty annotation, Field field) {
        if (!annotation.propertyName().isEmpty()) {
            return annotation.propertyName();
        }
        
        String className = field.getDeclaringClass().getSimpleName();
        String fieldName = field.getName();
        return className + "." + fieldName;
    }
    
    private static Object convertValue(String value, Class<?> targetType, 
                                       ConfigProperty.PropertyType annotationType, 
                                       Field field) {
        if (annotationType != ConfigProperty.PropertyType.AUTO) {
            return convertByAnnotationType(value, annotationType, field);
        }
        
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
            try {
                return new java.text.SimpleDateFormat("dd.MM.yyyy").parse(value);
            } catch (Exception e) {
                return new Date();
            }
        } else if (targetType.isArray()) {
            return convertArray(value, targetType.getComponentType());
        } else if (Collection.class.isAssignableFrom(targetType)) {
            return convertCollection(value, field);
        }
        
        return value;
    }
    
    private static Object convertByAnnotationType(String value, 
                                                  ConfigProperty.PropertyType type,
                                                  Field field) {
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
                try {
                    return new java.text.SimpleDateFormat("dd.MM.yyyy").parse(value);
                } catch (Exception e) {
                    return new Date();
                }
            case ARRAY:
                return convertArray(value, String.class);
            case LIST:
                return Arrays.asList(value.split(","));
            default:
                return value;
        }
    }
    
    private static Object convertArray(String value, Class<?> componentType) {
        String[] parts = value.split(",");
        if (componentType == String.class) {
            return parts;
        } else if (componentType == int.class || componentType == Integer.class) {
            int[] array = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                array[i] = Integer.parseInt(parts[i].trim());
            }
            return array;
        }
        return parts;
    }
    
    private static Collection<?> convertCollection(String value, Field field) {
        String[] parts = value.split(",");
        Class<?> genericType = getCollectionGenericType(field);
        
        List<Object> list = new ArrayList<>();
        for (String part : parts) {
            list.add(convertSimpleValue(part.trim(), genericType));
        }
        
        if (field.getType() == List.class || field.getType() == Collection.class) {
            return list;
        } else if (field.getType() == Set.class) {
            return new HashSet<>(list);
        }
        
        return list;
    }
    
    private static Class<?> getCollectionGenericType(Field field) {
        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) field.getGenericType();
            return (Class<?>) paramType.getActualTypeArguments()[0];
        }
        return String.class;
    }
    
    private static Object convertSimpleValue(String value, Class<?> type) {
        if (type == String.class) {
            return value;
        } else if (type == Integer.class || type == int.class) {
            return Integer.parseInt(value);
        } else if (type == Long.class || type == long.class) {
            return Long.parseLong(value);
        } else if (type == Boolean.class || type == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        return value;
    }
    
    private static void injectDependencies() {
        System.out.println("Внедрение зависимостей...");
        for (Object component : components.values()) {
            try {
                injectFieldDependencies(component);
                applyConfiguration(component);
            } catch (Exception e) {
                System.err.println("Ошибка внедрения зависимостей в " + 
                    component.getClass().getName() + ": " + e.getMessage());
            }
        }
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
}
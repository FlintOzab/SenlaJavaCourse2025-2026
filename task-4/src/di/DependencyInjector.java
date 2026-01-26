package di;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

import di.annotation.Component;
import di.annotation.Inject;

public class DependencyInjector {
    private final Map<String, Object> components = new HashMap<>();
    private final Map<Class<?>, Object> componentsByType = new HashMap<>();
    private final Set<Object> allInstances = new HashSet<>();
    
    public DependencyInjector() {
    }
    
    public static class Builder {
        private final List<Class<?>> componentClasses = new ArrayList<>();
        private final Map<Class<?>, Object> componentInstances = new HashMap<>();
        
        public Builder withComponent(Class<?> componentClass) {
            componentClasses.add(componentClass);
            return this;
        }
        
        public Builder withComponentInstance(Class<?> componentClass, Object instance) {
            componentInstances.put(componentClass, instance);
            return this;
        }
        
        public DependencyInjector build() {
        	DependencyInjector container = new DependencyInjector();
            
            for (Map.Entry<Class<?>, Object> entry : componentInstances.entrySet()) {
                container.registerComponentInternal(entry.getKey(), entry.getValue());
            }
            
            for (Class<?> componentClass : componentClasses) {
                if (!componentInstances.containsKey(componentClass)) {
                    try {
                        Object instance = container.createComponent(componentClass);
                        container.registerComponentInternal(componentClass, instance);
                    } catch (Exception e) {
                        throw new RuntimeException("Ошибка создания компонента " + 
                            componentClass.getName(), e);
                    }
                }
            }
            
            return container;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    private void registerComponentInternal(Class<?> componentClass, Object instance) {
        String name = getComponentName(componentClass);
        components.put(name, instance);
        componentsByType.put(componentClass, instance);
        allInstances.add(instance);
        
        for (Class<?> iface : componentClass.getInterfaces()) {
            componentsByType.put(iface, instance);
        }
    }
    
    public <T> T getComponent(Class<T> componentClass) {
        Object component = componentsByType.get(componentClass);
        if (component != null && componentClass.isInstance(component)) {
            return componentClass.cast(component);
        }
        throw new RuntimeException("Компонент не найден: " + componentClass.getName());
    }
    
    public <T> T getComponent(String name) {
        Object component = components.get(name);
        if (component != null) {
            return (T) component;
        }
        throw new RuntimeException("Компонент не найден: " + name);
    }
    
    private Object createComponent(Class<?> componentClass) throws Exception {
        Constructor<?>[] constructors = componentClass.getConstructors();
        Constructor<?> injectConstructor = null;
        
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                injectConstructor = constructor;
                break;
            }
        }
        
        Object instance;
        if (injectConstructor != null) {
            Class<?>[] paramTypes = injectConstructor.getParameterTypes();
            Object[] params = new Object[paramTypes.length];
            
            for (int i = 0; i < paramTypes.length; i++) {
                params[i] = getComponent(paramTypes[i]);
            }
            
            instance = injectConstructor.newInstance(params);
        } else {
            instance = componentClass.getDeclaredConstructor().newInstance();
        }
        
        injectFieldDependencies(instance);
        
        return instance;
    }
    
    private void injectFieldDependencies(Object instance) throws IllegalAccessException {
        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Inject injectAnnotation = field.getAnnotation(Inject.class);
                String dependencyName = injectAnnotation.value();
                
                Object dependency;
                if (!dependencyName.isEmpty()) {
                    dependency = getComponent(dependencyName);
                } else {
                    dependency = getComponent(field.getType());
                }
                
                if (dependency != null) {
                    field.set(instance, dependency);
                } else {
                    throw new RuntimeException("Не удалось найти зависимость для поля " + 
                        field.getName() + " типа " + field.getType().getName());
                }
            }
        }
    }
    
    private String getComponentName(Class<?> componentClass) {
        Component componentAnnotation = componentClass.getAnnotation(Component.class);
        if (componentAnnotation != null && !componentAnnotation.name().isEmpty()) {
            return componentAnnotation.name();
        }
        return componentClass.getName();
    }
    
    public Set<Object> getAllInstances() {
        return new HashSet<>(allInstances);
    }
    
    public List<Object> getComponentsByAnnotation(Class<?> annotationClass) {
        List<Object> result = new ArrayList<>();
        for (Object instance : allInstances) {
            if (instance.getClass().isAnnotationPresent((Class) annotationClass)) {
                result.add(instance);
            }
        }
        return result;
    }
    
    public void registerComponent(Object component) {
        registerComponentInternal(component.getClass(), component);
    }
}
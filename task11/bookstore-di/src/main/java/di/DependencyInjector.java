package di;

import di.annotation.Component;
import di.annotation.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Dependency injection container that manages components and their dependencies.
 * Provides automatic dependency resolution and component lifecycle management.
 *
 * @author Bookstore Team
 * @version 1.0
 */
public class DependencyInjector {

    /** Map of components by name. */
    private final Map<String, Object> components;

    /** Map of components by type. */
    private final Map<Class<?>, Object> componentsByType;

    /** Set of all component instances. */
    private final Set<Object> allInstances;

    /**
     * Creates a new empty dependency injector.
     */
    public DependencyInjector() {
        this.components = new HashMap<>();
        this.componentsByType = new HashMap<>();
        this.allInstances = new HashSet<>();
    }

    /**
     * Builder class for constructing a DependencyInjector with components.
     */
    public static class Builder {

        /** List of component classes to instantiate. */
        private final List<Class<?>> componentClasses;

        /** Map of pre-instantiated component instances. */
        private final Map<Class<?>, Object> componentInstances;

        /**
         * Creates a new builder instance.
         */
        public Builder() {
            this.componentClasses = new ArrayList<>();
            this.componentInstances = new HashMap<>();
        }

        /**
         * Adds a component class to be instantiated by the container.
         *
         * @param componentClass the component class
         * @return this builder
         */
        public Builder withComponent(final Class<?> componentClass) {
            componentClasses.add(componentClass);
            return this;
        }

        /**
         * Adds a pre-instantiated component instance.
         *
         * @param componentClass the component class
         * @param instance the component instance
         * @return this builder
         */
        public Builder withComponentInstance(final Class<?> componentClass, final Object instance) {
            componentInstances.put(componentClass, instance);
            return this;
        }

        /**
         * Builds the dependency injector with all registered components.
         *
         * @return the configured dependency injector
         */
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
                        throw new RuntimeException(
                            DIConstants.ERROR_COMPONENT_CREATION + componentClass.getName(), e);
                    }
                }
            }

            return container;
        }
    }

    /**
     * Creates a new builder instance.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Registers a component internally.
     *
     * @param componentClass the component class
     * @param instance the component instance
     */
    private void registerComponentInternal(final Class<?> componentClass, final Object instance) {
        String name = getComponentName(componentClass);
        components.put(name, instance);
        componentsByType.put(componentClass, instance);
        allInstances.add(instance);

        for (Class<?> iface : componentClass.getInterfaces()) {
            componentsByType.put(iface, instance);
        }
    }

    /**
     * Retrieves a component by its class.
     *
     * @param <T> the component type
     * @param componentClass the component class
     * @return the component instance
     * @throws RuntimeException if component not found
     */
    public <T> T getComponent(final Class<T> componentClass) {
        Object component = componentsByType.get(componentClass);
        if (component != null && componentClass.isInstance(component)) {
            return componentClass.cast(component);
        }
        throw new RuntimeException(DIConstants.ERROR_COMPONENT_NOT_FOUND + componentClass.getName());
    }

    /**
     * Retrieves a component by its name.
     *
     * @param <T> the component type
     * @param name the component name
     * @return the component instance
     * @throws RuntimeException if component not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getComponent(final String name) {
        Object component = components.get(name);
        if (component != null) {
            return (T) component;
        }
        throw new RuntimeException(DIConstants.ERROR_COMPONENT_NOT_FOUND_BY_NAME + name);
    }

    /**
     * Creates a component instance with dependency injection.
     *
     * @param componentClass the component class
     * @return the created instance
     * @throws Exception if creation fails
     */
    private Object createComponent(final Class<?> componentClass) throws Exception {
        Constructor<?>[] constructors = componentClass.getConstructors();
        Constructor<?> injectConstructor = findInjectConstructor(constructors);

        Object instance;
        if (injectConstructor != null) {
            instance = createWithConstructor(injectConstructor);
        } else {
            instance = createWithDefaultConstructor(componentClass);
        }

        injectFieldDependencies(instance);
        return instance;
    }

    /**
     * Finds constructor annotated with @Inject.
     *
     * @param constructors array of constructors to search
     * @return the first constructor with @Inject or null
     */
    private Constructor<?> findInjectConstructor(final Constructor<?>[] constructors) {
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                return constructor;
            }
        }
        return null;
    }

    /**
     * Creates an instance using the specified constructor.
     *
     * @param constructor the constructor to use
     * @return the created instance
     * @throws Exception if instantiation fails
     */
    private Object createWithConstructor(final Constructor<?> constructor) throws Exception {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Object[] params = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            params[i] = getComponent(paramTypes[i]);
        }

        return constructor.newInstance(params);
    }

    /**
     * Creates an instance using the default constructor.
     *
     * @param componentClass the component class
     * @return the created instance
     * @throws Exception if instantiation fails
     */
    private Object createWithDefaultConstructor(final Class<?> componentClass) throws Exception {
        return componentClass.getDeclaredConstructor().newInstance();
    }

    /**
     * Injects dependencies into fields annotated with @Inject.
     *
     * @param instance the component instance
     * @throws IllegalAccessException if field access fails
     */
    private void injectFieldDependencies(final Object instance) throws IllegalAccessException {
        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Inject injectAnnotation = field.getAnnotation(Inject.class);
                String dependencyName = injectAnnotation.value();

                Object dependency;
                if (!DIConstants.INJECT_VALUE_DEFAULT.equals(dependencyName)) {
                    dependency = getComponent(dependencyName);
                } else {
                    dependency = getComponent(field.getType());
                }

                if (dependency != null) {
                    field.set(instance, dependency);
                } else {
                    throw new RuntimeException(
                        DIConstants.ERROR_DEPENDENCY_NOT_FOUND + field.getName()
                        + DIConstants.ERROR_FIELD_TYPE_SUFFIX + field.getType().getName());
                }
            }
        }
    }

    /**
     * Gets the component name from annotation or class name.
     *
     * @param componentClass the component class
     * @return the component name
     */
    private String getComponentName(final Class<?> componentClass) {
        Component componentAnnotation = componentClass.getAnnotation(Component.class);
        if (componentAnnotation != null
                && !DIConstants.COMPONENT_NAME_DEFAULT.equals(componentAnnotation.name())) {
            return componentAnnotation.name();
        }
        return componentClass.getName();
    }

    /**
     * Returns all component instances.
     *
     * @return set of all component instances
     */
    public Set<Object> getAllInstances() {
        return new HashSet<>(allInstances);
    }

    /**
     * Returns components annotated with a specific annotation.
     *
     * @param annotationClass the annotation class
     * @return list of components with the annotation
     */
    public List<Object> getComponentsByAnnotation(final Class<?> annotationClass) {
        List<Object> result = new ArrayList<>();
        for (Object instance : allInstances) {
            if (instance.getClass().isAnnotationPresent(
                    annotationClass.asSubclass(java.lang.annotation.Annotation.class))) {
                result.add(instance);
            }
        }
        return result;
    }

    /**
     * Registers a component instance.
     *
     * @param component the component instance
     */
    public void registerComponent(final Object component) {
        registerComponentInternal(component.getClass(), component);
    }
}

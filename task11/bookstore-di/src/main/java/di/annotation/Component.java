package di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import di.DIConstants;

/**
 * Marks a class as a component for dependency injection.
 * Components are automatically discovered and managed by the DI container.
 *
 * @author Bookstore Team
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

    /**
     * Optional name for the component.
     * If not specified, the class name will be used.
     *
     * @return the component name
     */
    String name() default DIConstants.COMPONENT_NAME_DEFAULT;
}

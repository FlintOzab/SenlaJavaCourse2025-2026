package di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import di.DIConstants;

/**
 * Marks a constructor or field for dependency injection.
 *
 * @author Bookstore Team
 * @version 1.0
 */
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {

    /**
     * Optional name for the dependency.
     * If not specified, the type will be used for lookup.
     *
     * @return the dependency name
     */
    String value() default DIConstants.INJECT_VALUE_DEFAULT;
}

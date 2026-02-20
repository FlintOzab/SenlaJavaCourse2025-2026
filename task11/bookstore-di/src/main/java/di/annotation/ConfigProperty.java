package di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import di.DIConstants;

/**
 * Marks a field to be injected with a value from a configuration file.
 *
 * @author Bookstore Team
 * @version 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {

    /**
     * The name of the configuration file.
     *
     * @return the configuration file name
     */
    String configFileName() default DIConstants.CONFIG_FILE_NAME_DEFAULT;

    /**
     * The name of the property in the configuration file.
     * If not specified, the field name will be used.
     *
     * @return the property name
     */
    String propertyName() default DIConstants.CONFIG_PROPERTY_NAME_DEFAULT;

    /**
     * The expected type of the property value.
     *
     * @return the property type
     */
    PropertyType type() default PropertyType.AUTO;

    /**
     * Enumeration of supported property types.
     */
    enum PropertyType {
        /** String type. */
        STRING,

        /** Integer type. */
        INTEGER,

        /** Long type. */
        LONG,

        /** Boolean type. */
        BOOLEAN,

        /** Double type. */
        DOUBLE,

        /** Date type. */
        DATE,

        /** Array type. */
        ARRAY,

        /** List type. */
        LIST,

        /** Set type. */
        SET,

        /** Auto-detect type. */
        AUTO
    }
}

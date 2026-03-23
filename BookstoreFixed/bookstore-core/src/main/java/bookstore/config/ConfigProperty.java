package bookstore.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {
    String configFileName() default "bookstore.properties";
    String propertyName() default "";
    PropertyType type() default PropertyType.AUTO;
    
    enum PropertyType {
        STRING,
        INTEGER,
        LONG,
        BOOLEAN,
        DOUBLE,
        DATE,
        ARRAY,
        LIST,
        SET,
        AUTO
    }
}
package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import jpa.JpaConstants;

/**
 * Spring configuration class for the Bookstore application.
 * Configures component scanning and property placeholders.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Configuration
@ComponentScan(basePackages = {
    "service", 
    "app", 
    "app.control", 
    "view", 
    "config", 
    "dao", 
    "jdbc", 
    "jpa", 
    "util"
})
@PropertySource("classpath:bookstore.properties")
@EnableTransactionManagement
public class AppConfig {

    /**
     * Creates a PropertySourcesPlaceholderConfigurer bean.
     * This bean enables the use of @Value annotations for property injection.
     * 
     * @return the property sources placeholder configurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreUnresolvablePlaceholders(true);
        configurer.setNullValue("");
        return configurer;
    }
    
    /**
     * Creates an EntityManagerFactory bean for JPA.
     * 
     * @return the entity manager factory bean
     */
    @Bean
    public LocalEntityManagerFactoryBean entityManagerFactory() {
        LocalEntityManagerFactoryBean factoryBean = new LocalEntityManagerFactoryBean();
        factoryBean.setPersistenceUnitName(JpaConstants.PERSISTENCE_UNIT_NAME);
        return factoryBean;
    }
    
    /**
     * Creates a transaction manager for JPA.
     * 
     * @param entityManagerFactory the entity manager factory
     * @return the transaction manager
     */
    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
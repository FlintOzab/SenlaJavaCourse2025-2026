package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

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
	    "dao.jpa",
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
     * Creates a DataSource bean for database connection.
     * 
     * @return the data source
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/bookstore_db");
        dataSource.setUsername("test");
        dataSource.setPassword("test");
        return dataSource;
    }
    
    /**
     * Creates JPA vendor adapter with Hibernate.
     * 
     * @return the JPA vendor adapter
     */
    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(true);
        adapter.setGenerateDdl(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
        return adapter;
    }
    
    /**
     * Creates an EntityManagerFactory bean for JPA.
     * 
     * @param dataSource the data source
     * @param jpaVendorAdapter the JPA vendor adapter
     * @return the entity manager factory bean
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            HibernateJpaVendorAdapter jpaVendorAdapter) {
        
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("model");
        em.setJpaVendorAdapter(jpaVendorAdapter);
        
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("hibernate.hbm2ddl.auto", "update");
        jpaProperties.setProperty("hibernate.show_sql", "true");
        jpaProperties.setProperty("hibernate.format_sql", "true");
        jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        
        jpaProperties.setProperty("hibernate.c3p0.min_size", "5");
        jpaProperties.setProperty("hibernate.c3p0.max_size", "20");
        jpaProperties.setProperty("hibernate.c3p0.timeout", "300");
        jpaProperties.setProperty("hibernate.c3p0.max_statements", "50");
        
        em.setJpaProperties(jpaProperties);
        
        return em;
    }
    
    /**
     * Creates a transaction manager for JPA.
     * 
     * @param entityManagerFactory the entity manager factory
     * @return the transaction manager
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
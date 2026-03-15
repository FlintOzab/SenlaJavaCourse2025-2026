package bookstore.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Import; 
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import bookstore.app.control.BookstoreController;
import bookstore.app.control.ConsoleMenuController;
import bookstore.config.BookstoreConfig;
import bookstore.dao.jpa.JpaBookDAO;
import bookstore.dao.jpa.JpaBookDAOInterface;
import bookstore.dao.jpa.JpaDAOFactory;
import bookstore.dao.jpa.JpaOrderDAO;
import bookstore.dao.jpa.JpaOrderDAOInterface;
import bookstore.dao.jpa.JpaRequestDAO;
import bookstore.dao.jpa.JpaRequestDAOInterface;
import bookstore.service.Bookstore;
import bookstore.service.CSVService;
import bookstore.service.StateManager;
import bookstore.view.ConsoleDisplay;
import bookstore.view.ConsoleInput;
import bookstore.flyway.FlywayConfig;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Spring configuration class for the Bookstore application.
 * Configures all beans explicitly using properties file.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Configuration
@PropertySource("classpath:bookstore.properties")
@PropertySource(value = "file:./config/bookstore.properties", ignoreResourceNotFound = true)
@EnableTransactionManagement
@Import(FlywayConfig.class)
public class AppConfig {

    private final Environment env;

    public AppConfig(Environment env) {
        this.env = env;
    }

    /**
     * Creates a PropertySourcesPlaceholderConfigurer bean.
     * This bean enables the use of @Value annotations for property injection.
     * 
     * @return the property sources placeholder configurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    /**
     * Creates a DataSource bean for database connection using properties.
     * 
     * @return the data source
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("db.driver"));
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));
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
        adapter.setShowSql(Boolean.parseBoolean(env.getProperty("hibernate.show_sql")));
        adapter.setGenerateDdl(false); 
        adapter.setDatabasePlatform(env.getProperty("hibernate.dialect"));
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
    @DependsOn("flywayConfig")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            HibernateJpaVendorAdapter jpaVendorAdapter,
            FlywayConfig unused) 
    {
        
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("bookstore.model");
        em.setJpaVendorAdapter(jpaVendorAdapter);
        
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        jpaProperties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        jpaProperties.setProperty("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        jpaProperties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
        
        jpaProperties.setProperty("hibernate.c3p0.min_size", env.getProperty("hibernate.c3p0.min_size"));
        jpaProperties.setProperty("hibernate.c3p0.max_size", env.getProperty("hibernate.c3p0.max_size"));
        jpaProperties.setProperty("hibernate.c3p0.timeout", env.getProperty("hibernate.c3p0.timeout"));
        jpaProperties.setProperty("hibernate.c3p0.max_statements", env.getProperty("hibernate.c3p0.max_statements"));
        
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
    
    /**
     * Creates a Properties bean with connection properties.
     * This bean is used by FlywayConfig.
     * 
     * @return the connection properties
     */
    @Bean
    public Properties connectionProperties() {
        Properties props = new Properties();
        props.setProperty("db.url", env.getProperty("db.url"));
        props.setProperty("db.username", env.getProperty("db.username"));
        props.setProperty("db.password", env.getProperty("db.password"));
        return props;
    }
    
    /**
     * Creates BookstoreConfig bean.
     * 
     * @return the bookstore configuration
     */
    @Bean
    public BookstoreConfig bookstoreConfig() {
        return new BookstoreConfig();
    }
    
    /**
     * Creates JpaBookDAO bean.
     * 
     * @return the book DAO
     */
    @Bean
    public JpaBookDAOInterface jpaBookDAO() {
        return new JpaBookDAO();
    }
    
    /**
     * Creates JpaOrderDAO bean.
     * 
     * @return the order DAO
     */
    @Bean
    public JpaOrderDAOInterface jpaOrderDAO() {
        return new JpaOrderDAO();
    }
    
    /**
     * Creates JpaRequestDAO bean.
     * 
     * @return the request DAO
     */
    @Bean
    public JpaRequestDAOInterface jpaRequestDAO() {
        return new JpaRequestDAO();
    }
    
    /**
     * Creates JpaDAOFactory bean.
     * 
     * @param bookDAO the book DAO
     * @param orderDAO the order DAO
     * @param requestDAO the request DAO
     * @return the DAO factory
     */
    @Bean
    public JpaDAOFactory jpaDAOFactory(
            JpaBookDAOInterface bookDAO,
            JpaOrderDAOInterface orderDAO,
            JpaRequestDAOInterface requestDAO) {
        return new JpaDAOFactory(bookDAO, orderDAO, requestDAO);
    }
    
    /**
     * Creates Bookstore service bean.
     * 
     * @param daoFactory the DAO factory
     * @param config the bookstore configuration
     * @return the bookstore service
     */
    @Bean
    public Bookstore bookstore(
            JpaDAOFactory daoFactory,
            BookstoreConfig config) {
        return new Bookstore(daoFactory, config);
    }
    
    /**
     * Creates StateManager bean.
     * 
     * @param bookstore the bookstore service
     * @return the state manager
     */
    @Bean
    public StateManager stateManager(Bookstore bookstore) {
        return new StateManager(bookstore);
    }
    
    /**
     * Creates CSVService bean.
     * 
     * @return the CSV service
     */
    @Bean
    public CSVService csvService() {
        return new CSVService();
    }
    
    /**
     * Creates ConsoleDisplay bean.
     * 
     * @return the console display
     */
    @Bean
    public ConsoleDisplay consoleDisplay() {
        return new ConsoleDisplay();
    }
    
    /**
     * Creates ConsoleInput bean.
     * 
     * @return the console input
     */
    @Bean
    public ConsoleInput consoleInput() {
        return new ConsoleInput();
    }
    
    /**
     * Creates BookstoreController bean.
     * 
     * @param bookstore the bookstore service
     * @param stateManager the state manager
     * @return the bookstore controller
     */
    @Bean
    public BookstoreController bookstoreController(
            Bookstore bookstore,
            StateManager stateManager) {
        return new BookstoreController(bookstore, stateManager);
    }
    
    /**
     * Creates ConsoleMenuController bean.
     * 
     * @param display the console display
     * @param input the console input
     * @return the menu controller
     */
    @Bean
    public ConsoleMenuController consoleMenuController(
            ConsoleDisplay display,
            ConsoleInput input) {
        return new ConsoleMenuController(display, input);
    }
    
    /**
     * Creates ShopApp bean.
     * 
     * @param controller the bookstore controller
     * @param menuController the menu controller
     * @param display the console display
     * @param input the console input
     * @param stateManager the state manager
     * @return the shop application
     */
    @Bean
    public ShopApp shopApp(
            BookstoreController controller,
            ConsoleMenuController menuController,
            ConsoleDisplay display,
            ConsoleInput input,
            StateManager stateManager) {
        return new ShopApp(controller, menuController, display, input, stateManager);
    }
}
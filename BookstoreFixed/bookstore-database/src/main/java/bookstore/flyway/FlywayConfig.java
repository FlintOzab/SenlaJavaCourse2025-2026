package bookstore.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import jakarta.annotation.PostConstruct;
import java.util.Properties;

/**
 * Конфигурация Flyway для управления миграциями БД.
 * Автоматически запускает миграции при старте приложения.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Configuration
@DependsOn("connectionProperties")
public class FlywayConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayConfig.class);
    
    private final Properties connectionProperties;
    
    @Autowired
    public FlywayConfig(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }
    
    @PostConstruct
    public void migrateDatabase() {
        LOGGER.info("Запуск Flyway миграций...");
        
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(
                        connectionProperties.getProperty("db.url"),
                        connectionProperties.getProperty("db.username"),
                        connectionProperties.getProperty("db.password")
                    )
                    .locations("classpath:migration")
                    .baselineOnMigrate(true)
                    .table("flyway_schema_history")
                    .encoding("UTF-8")
                    .validateOnMigrate(true)
                    .outOfOrder(false)
                    .load();
            var info = flyway.info();
            LOGGER.info("Текущая версия: {}", info.current() != null ? info.current().getVersion() : "none");
            LOGGER.info("Ожидающие миграции: {}", info.pending().length);
            if (info.pending().length > 0) {
            	flyway.repair();
                MigrateResult migrationsCount = flyway.migrate();
                LOGGER.info("Flyway успешно выполнил {} миграций", migrationsCount.migrationsExecuted);
            } else {
                LOGGER.info("Нет ожидающих миграций");
            }
            
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении Flyway миграций", e);
            throw new RuntimeException("Не удалось выполнить миграции БД", e);
        }
    }
}
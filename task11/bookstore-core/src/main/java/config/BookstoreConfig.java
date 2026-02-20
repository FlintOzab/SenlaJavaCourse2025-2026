package config;

import di.annotation.Component;
import di.annotation.ConfigProperty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Configuration class for bookstore application.
 * Manages all configurable parameters loaded from properties file.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class BookstoreConfig {
    
    /** Date format for property files. */
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    
    /** Default stale months threshold. */
    private static final int DEFAULT_STALE_THRESHOLD = 6;
    
    /** Default auto fulfill requests setting. */
    private static final boolean DEFAULT_AUTO_FULFILL = true;
    
    /** Default book price. */
    private static final long DEFAULT_BOOK_PRICE = 100L;
    
    /** Default export directory. */
    private static final String DEFAULT_EXPORT_DIR = "./exports/";
    
    /** Default max books per order. */
    private static final int DEFAULT_MAX_BOOKS = 10;
    
    /** Stale months threshold property. */
    @ConfigProperty(propertyName = "stale.months.threshold", 
                    type = ConfigProperty.PropertyType.INTEGER)
    private int staleMonthsThreshold;
    
    /** Auto fulfill requests property. */
    @ConfigProperty(propertyName = "auto.fulfill.requests", 
                    type = ConfigProperty.PropertyType.BOOLEAN)
    private boolean autoFulfillRequests;
    
    /** Default book price property. */
    @ConfigProperty(propertyName = "default.book.price", 
                    type = ConfigProperty.PropertyType.LONG)
    private long defaultBookPrice;
    
    /** Export directory property. */
    @ConfigProperty(propertyName = "export.directory")
    private String exportDirectory;
    
    /** Max books per order property. */
    @ConfigProperty(propertyName = "max.books.per.order", 
                    type = ConfigProperty.PropertyType.INTEGER)
    private int maxBooksPerOrder;
    
    /** Allowed formats property. */
    @ConfigProperty(propertyName = "allowed.formats", 
                    type = ConfigProperty.PropertyType.LIST)
    private List<String> allowedFormats;
    
    /** Discount rates property. */
    @ConfigProperty(propertyName = "discount.rates", 
                    type = ConfigProperty.PropertyType.ARRAY)
    private Double[] discountRates;
    
    /** Maintenance date property. */
    @ConfigProperty(propertyName = "maintenance.date", 
                    type = ConfigProperty.PropertyType.DATE)
    private Date maintenanceDate;
    
    /**
     * Constructs a new BookstoreConfig with default values.
     */
    public BookstoreConfig() {
        this.staleMonthsThreshold = DEFAULT_STALE_THRESHOLD;
        this.autoFulfillRequests = DEFAULT_AUTO_FULFILL;
        this.defaultBookPrice = DEFAULT_BOOK_PRICE;
        this.exportDirectory = DEFAULT_EXPORT_DIR;
        this.maxBooksPerOrder = DEFAULT_MAX_BOOKS;
    }
    
    /**
     * Gets the stale months threshold.
     * 
     * @return the stale months threshold
     */
    public int getStaleMonthsThreshold() { 
        return staleMonthsThreshold; 
    }
    
    /**
     * Checks if auto fulfill requests is enabled.
     * 
     * @return true if auto fulfill is enabled
     */
    public boolean isAutoFulfillRequests() { 
        return autoFulfillRequests; 
    }
    
    /**
     * Gets the default book price.
     * 
     * @return the default book price
     */
    public long getDefaultBookPrice() { 
        return defaultBookPrice; 
    }
    
    /**
     * Gets the export directory.
     * 
     * @return the export directory
     */
    public String getExportDirectory() { 
        return exportDirectory; 
    }
    
    /**
     * Gets the maximum books per order.
     * 
     * @return the maximum books per order
     */
    public int getMaxBooksPerOrder() {
        return maxBooksPerOrder;
    }
    
    /**
     * Gets the allowed formats.
     * 
     * @return the allowed formats
     */
    public List<String> getAllowedFormats() {
        return allowedFormats;
    }
    
    /**
     * Gets the discount rates.
     * 
     * @return the discount rates
     */
    public Double[] getDiscountRates() {
        return discountRates;
    }
    
    /**
     * Gets the maintenance date.
     * 
     * @return the maintenance date
     */
    public Date getMaintenanceDate() {
        return maintenanceDate;
    }
    
    /**
     * Sets the stale months threshold.
     * 
     * @param threshold the new threshold
     */
    public void setStaleMonthsThreshold(final int threshold) {
        this.staleMonthsThreshold = threshold;
    }
    
    /**
     * Sets auto fulfill requests.
     * 
     * @param autoFulfill the new setting
     */
    public void setAutoFulfillRequests(final boolean autoFulfill) {
        this.autoFulfillRequests = autoFulfill;
    }
    
    /**
     * Sets the default book price.
     * 
     * @param price the new default price
     */
    public void setDefaultBookPrice(final long price) {
        this.defaultBookPrice = price;
    }
    
    /**
     * Sets the export directory and creates it if it doesn't exist.
     * 
     * @param directory the new export directory
     */
    public void setExportDirectory(final String directory) {
        this.exportDirectory = directory;
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Saves the current configuration to properties file.
     */
    public void savePropertiesToFile() {
        Properties properties = new Properties();
        properties.setProperty("stale.months.threshold", 
            String.valueOf(staleMonthsThreshold));
        properties.setProperty("auto.fulfill.requests", 
            String.valueOf(autoFulfillRequests));
        properties.setProperty("default.book.price", 
            String.valueOf(defaultBookPrice));
        properties.setProperty("export.directory", exportDirectory);
        properties.setProperty("max.books.per.order", 
            String.valueOf(maxBooksPerOrder));
        
        if (allowedFormats != null) {
            properties.setProperty("allowed.formats", 
                String.join(",", allowedFormats));
        }
        
        if (discountRates != null) {
            List<String> rates = new ArrayList<>();
            for (Double rate : discountRates) {
                rates.add(String.valueOf(rate));
            }
            properties.setProperty("discount.rates", 
                String.join(",", rates));
        }
        
        if (maintenanceDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            properties.setProperty("maintenance.date", 
                dateFormat.format(maintenanceDate));
        }
        
        try (OutputStream output = new FileOutputStream("bookstore.properties")) {
            properties.store(output, "Bookstore Configuration");
        } catch (IOException e) {
            System.out.println("Ошибка сохранения конфигурации: " + e.getMessage());
        }
    }
}
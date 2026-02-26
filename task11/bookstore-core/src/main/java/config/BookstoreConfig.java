package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    @Value("${stale.months.threshold:6}")
    private int staleMonthsThreshold;
    
    /** Auto fulfill requests property. */
    @Value("${auto.fulfill.requests:true}")
    private boolean autoFulfillRequests;
    
    /** Default book price property. */
    @Value("${default.book.price:100}")
    private long defaultBookPrice;
    
    /** Export directory property. */
    @Value("${export.directory:./exports/}")
    private String exportDirectory;
    
    /** Max books per order property. */
    @Value("${max.books.per.order:10}")
    private int maxBooksPerOrder;
    
    /** Allowed formats property. */
    @Value("${allowed.formats:}")
    private String allowedFormatsString;
    
    /** Discount rates property. */
    @Value("${discount.rates:}")
    private String discountRatesString;
    
    /** Maintenance date property. */
    @Value("${maintenance.date:}")
    private String maintenanceDateString;
    
    /** Allowed formats list. */
    private List<String> allowedFormats;
    
    /** Discount rates array. */
    private Double[] discountRates;
    
    /** Maintenance date. */
    private Date maintenanceDate;
    
    /**
     * Constructs a new BookstoreConfig with default values.
     */
    public BookstoreConfig() {
        // Initialize parsed fields after property injection
        parseComplexProperties();
    }
    
    /**
     * Post-construct method to parse complex properties.
     */
    private void parseComplexProperties() {
        // Parse allowed formats
        if (allowedFormatsString != null && !allowedFormatsString.isEmpty()) {
            String[] parts = allowedFormatsString.split(",");
            allowedFormats = new ArrayList<>();
            for (String part : parts) {
                allowedFormats.add(part.trim());
            }
        }
        
        // Parse discount rates
        if (discountRatesString != null && !discountRatesString.isEmpty()) {
            String[] parts = discountRatesString.split(",");
            List<Double> rates = new ArrayList<>();
            for (String part : parts) {
                try {
                    rates.add(Double.parseDouble(part.trim()));
                } catch (NumberFormatException e) {
                    // Ignore invalid values
                }
            }
            discountRates = rates.toArray(new Double[0]);
        }
        
        // Parse maintenance date
        if (maintenanceDateString != null && !maintenanceDateString.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                maintenanceDate = dateFormat.parse(maintenanceDateString);
            } catch (Exception e) {
                // Ignore parse errors
            }
        }
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
        
        if (allowedFormats != null && !allowedFormats.isEmpty()) {
            properties.setProperty("allowed.formats", 
                String.join(",", allowedFormats));
        }
        
        if (discountRates != null && discountRates.length > 0) {
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
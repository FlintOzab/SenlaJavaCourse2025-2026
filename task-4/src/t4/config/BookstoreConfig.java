package t4.config;

import di.annotation.Component;
import di.annotation.ConfigProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class BookstoreConfig {
    
    @ConfigProperty(propertyName = "stale.months.threshold", type = ConfigProperty.PropertyType.INTEGER)
    private int staleMonthsThreshold;
    
    @ConfigProperty(propertyName = "auto.fulfill.requests", type = ConfigProperty.PropertyType.BOOLEAN)
    private boolean autoFulfillRequests;
    
    @ConfigProperty(propertyName = "default.book.price", type = ConfigProperty.PropertyType.LONG)
    private long defaultBookPrice;
    
    @ConfigProperty(propertyName = "export.directory")
    private String exportDirectory;
    
    @ConfigProperty(propertyName = "max.books.per.order", type = ConfigProperty.PropertyType.INTEGER)
    private int maxBooksPerOrder;
    
    @ConfigProperty(propertyName = "allowed.formats", type = ConfigProperty.PropertyType.LIST)
    private List<String> allowedFormats;
    
    @ConfigProperty(propertyName = "discount.rates", type = ConfigProperty.PropertyType.ARRAY)
    private Double[] discountRates;
    
    @ConfigProperty(propertyName = "maintenance.date", type = ConfigProperty.PropertyType.DATE)
    private Date maintenanceDate;
    
    public BookstoreConfig() {
        this.staleMonthsThreshold = 6;
        this.autoFulfillRequests = true;
        this.defaultBookPrice = 100;
        this.exportDirectory = "./exports/";
        this.maxBooksPerOrder = 10;
    }
    
    public int getStaleMonthsThreshold() { 
        return staleMonthsThreshold; 
    }
    
    public boolean isAutoFulfillRequests() { 
        return autoFulfillRequests; 
    }
    
    public long getDefaultBookPrice() { 
        return defaultBookPrice; 
    }
    
    public String getExportDirectory() { 
        return exportDirectory; 
    }
    
    public int getMaxBooksPerOrder() {
        return maxBooksPerOrder;
    }
    
    public List<String> getAllowedFormats() {
        return allowedFormats;
    }
    
    public Double[] getDiscountRates() {
        return discountRates;
    }
    
    public Date getMaintenanceDate() {
        return maintenanceDate;
    }
    
    public void setStaleMonthsThreshold(int threshold) {
        this.staleMonthsThreshold = threshold;
    }
    
    public void setAutoFulfillRequests(boolean autoFulfill) {
        this.autoFulfillRequests = autoFulfill;
    }
    
    public void setDefaultBookPrice(long price) {
        this.defaultBookPrice = price;
    }
    
    public void setExportDirectory(String directory) {
        this.exportDirectory = directory;
        java.io.File dir = new java.io.File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public void savePropertiesToFile() {
        try (java.io.OutputStream output = new java.io.FileOutputStream("bookstore.properties")) {
            java.util.Properties properties = new java.util.Properties();
            properties.setProperty("stale.months.threshold", String.valueOf(staleMonthsThreshold));
            properties.setProperty("auto.fulfill.requests", String.valueOf(autoFulfillRequests));
            properties.setProperty("default.book.price", String.valueOf(defaultBookPrice));
            properties.setProperty("export.directory", exportDirectory);
            properties.setProperty("max.books.per.order", String.valueOf(maxBooksPerOrder));
            
            if (allowedFormats != null) {
                properties.setProperty("allowed.formats", String.join(",", allowedFormats));
            }
            
            if (discountRates != null) {
                List<String> rates = new ArrayList<>();
                for (Double rate : discountRates) {
                    rates.add(String.valueOf(rate));
                }
                properties.setProperty("discount.rates", String.join(",", rates));
            }
            
            if (maintenanceDate != null) {
                properties.setProperty("maintenance.date", 
                    new java.text.SimpleDateFormat("dd.MM.yyyy").format(maintenanceDate));
            }
            
            properties.store(output, "Bookstore Configuration");
        } catch (java.io.IOException e) {
            System.out.println("Ошибка сохранения конфигурации: " + e.getMessage());
        }
    }
}
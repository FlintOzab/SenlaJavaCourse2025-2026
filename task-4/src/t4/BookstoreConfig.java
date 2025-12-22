package t4;

import java.io.File;

@Component
public class BookstoreConfig {
    
    @ConfigProperty(propertyName = "stale.months.threshold")
    private int staleMonthsThreshold;
    
    @ConfigProperty(propertyName = "auto.fulfill.requests")
    private boolean autoFulfillRequests;
    
    @ConfigProperty(propertyName = "default.book.price")
    private long defaultBookPrice;
    
    @ConfigProperty(propertyName = "export.directory")
    private String exportDirectory;
    
    public BookstoreConfig() {
    }
    
    public void savePropertiesToFile() {
        try (java.io.OutputStream output = new java.io.FileOutputStream("bookstore.properties")) {
            java.util.Properties properties = new java.util.Properties();
            properties.setProperty("stale.months.threshold", String.valueOf(staleMonthsThreshold));
            properties.setProperty("auto.fulfill.requests", String.valueOf(autoFulfillRequests));
            properties.setProperty("default.book.price", String.valueOf(defaultBookPrice));
            properties.setProperty("export.directory", exportDirectory);
            properties.store(output, "Bookstore Configuration");
        } catch (java.io.IOException e) {
            System.out.println("Ошибка сохранения конфигурации: " + e.getMessage());
        }
    }
    
    public int getStaleMonthsThreshold() { return staleMonthsThreshold; }
    public boolean isAutoFulfillRequests() { return autoFulfillRequests; }
    public long getDefaultBookPrice() { return defaultBookPrice; }
    public String getExportDirectory() { return exportDirectory; }
    
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
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public void reload() {
    	DependencyInjector.initialize("t4");
    }
}
package t4;

import java.io.*;
import java.util.Properties;

public class BookstoreConfig {
	 private static final String CONFIG_FILE = "bookstore.properties";
	 private static BookstoreConfig instance;
	 private final Properties properties;
	 
	 private int staleMonthsThreshold;
	 private boolean autoFulfillRequests;
	 private long defaultBookPrice; 
	 private String exportDirectory;
	 
	 private BookstoreConfig() {
	     properties = new Properties();
	     loadDefaultProperties();
	     loadPropertiesFromFile();
	     parseProperties();
	 }
	 
	 public static BookstoreConfig getInstance() {
	     if (instance == null) {
	         instance = new BookstoreConfig();
	     }
	     return instance;
	 }
	 
	 private void loadDefaultProperties() {
	     properties.setProperty("stale.months.threshold", "6");
	     properties.setProperty("auto.fulfill.requests", "true");
	     properties.setProperty("default.book.price", "100");
	     properties.setProperty("export.directory", "./exports/");
	 }
	 
	 private void loadPropertiesFromFile() {
	     try (InputStream input = new FileInputStream(CONFIG_FILE)) {
	         properties.load(input);
	     } catch (FileNotFoundException e) {
	         System.out.println("Конфигурационный файл не найден, используются значения по умолчанию");
	         savePropertiesToFile();
	     } catch (IOException e) {
	         System.out.println("Ошибка чтения конфигурационного файла: " + e.getMessage());
	     }
	 }
	 
	 private void parseProperties() {
	     staleMonthsThreshold = Integer.parseInt(properties.getProperty("stale.months.threshold"));
	     autoFulfillRequests = Boolean.parseBoolean(properties.getProperty("auto.fulfill.requests"));
	     defaultBookPrice = Long.parseLong(properties.getProperty("default.book.price"));
	     exportDirectory = properties.getProperty("export.directory");
	     
	     File dir = new File(exportDirectory);
	     if (!dir.exists()) {
	         dir.mkdirs();
	     }
	 }
	 
	 public void savePropertiesToFile() {
	     try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
	         properties.store(output, "Bookstore Configuration");
	     } catch (IOException e) {
	         System.out.println("Ошибка сохранения конфигурации: " + e.getMessage());
	     }
	 }
	 
	 public int getStaleMonthsThreshold() { return staleMonthsThreshold; }
	 public boolean isAutoFulfillRequests() { return autoFulfillRequests; }
	 public long getDefaultBookPrice() { return defaultBookPrice; }
	 public String getExportDirectory() { return exportDirectory; }
	 
	 public void setStaleMonthsThreshold(int threshold) {
	     this.staleMonthsThreshold = threshold;
	     properties.setProperty("stale.months.threshold", String.valueOf(threshold));
	 }
	 
	 public void setAutoFulfillRequests(boolean autoFulfill) {
	     this.autoFulfillRequests = autoFulfill;
	     properties.setProperty("auto.fulfill.requests", String.valueOf(autoFulfill));
	 }
	 
	 public void setDefaultBookPrice(long price) {
	     this.defaultBookPrice = price;
	     properties.setProperty("default.book.price", String.valueOf(price));
	 }
	 
	 public void setExportDirectory(String directory) {
	     this.exportDirectory = directory;
	     properties.setProperty("export.directory", directory);
	 }
	 
	 public void reload() {
	     loadPropertiesFromFile();
	     parseProperties();
	 }
}
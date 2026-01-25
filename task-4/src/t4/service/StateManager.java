package t4.service;

import t4.exception.BookstoreException;

import java.io.*;

import di.annotation.Component;
import di.annotation.Inject;

@Component
public class StateManager {
    private static final String STATE_FILE = "bookstore_state.dat";
    private static final String BACKUP_FILE = STATE_FILE + ".backup";
    
    private final Bookstore bookstore;
    
    @Inject
    public StateManager(Bookstore bookstore) {
        this.bookstore = bookstore;
    }
    
    public void saveState() throws BookstoreException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(STATE_FILE))) {
            oos.writeObject(bookstore);
        } catch (IOException e) {
            throw new BookstoreException("Ошибка сохранения состояния: " + e.getMessage(), e);
        }
    }
    
    public static Bookstore loadState() throws BookstoreException {
        File stateFile = new File(STATE_FILE);
        if (!stateFile.exists() || stateFile.length() == 0) {
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(STATE_FILE))) {
            Bookstore bookstore = (Bookstore) ois.readObject();
            return bookstore;
        } catch (InvalidClassException | StreamCorruptedException e) {
            stateFile.delete();
            return null;
        } catch (IOException | ClassNotFoundException e) {
            throw new BookstoreException("Ошибка загрузки состояния: " + e.getMessage(), e);
        }
    }
    
    public void createBackup() throws BookstoreException {
        File stateFile = new File(STATE_FILE);
        if (!stateFile.exists()) {
            throw new BookstoreException("Нет файла состояния для создания резервной копии");
        }
        
        File backupFile = new File(BACKUP_FILE);
        try (InputStream in = new FileInputStream(stateFile);
             OutputStream out = new FileOutputStream(backupFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new BookstoreException("Ошибка создания резервной копии: " + e.getMessage(), e);
        }
    }
    
    public boolean restoreFromBackup() throws BookstoreException {
        File backupFile = new File(BACKUP_FILE);
        if (!backupFile.exists()) {
            throw new BookstoreException("Резервная копия не найдена");
        }
        
        File stateFile = new File(STATE_FILE);
        try (InputStream in = new FileInputStream(backupFile);
             OutputStream out = new FileOutputStream(stateFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            return true;
        } catch (IOException e) {
            throw new BookstoreException("Ошибка восстановления из резервной копии: " + e.getMessage(), e);
        }
    }
    
    public void deleteState() {
        File stateFile = new File(STATE_FILE);
        if (stateFile.exists()) {
            stateFile.delete();
        }
    }
    
    public boolean stateFileExists() {
        File stateFile = new File(STATE_FILE);
        return stateFile.exists() && stateFile.length() > 0;
    }
    
    public boolean backupFileExists() {
        File backupFile = new File(BACKUP_FILE);
        return backupFile.exists() && backupFile.length() > 0;
    }
    
    public long getStateFileSize() {
        File stateFile = new File(STATE_FILE);
        return stateFile.exists() ? stateFile.length() : 0;
    }
    
    public long getBackupFileSize() {
        File backupFile = new File(BACKUP_FILE);
        return backupFile.exists() ? backupFile.length() : 0;
    }
}
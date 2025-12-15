package t4;

import java.io.*;

public class StateManager {
    private static final String STATE_FILE = "bookstore_state.dat";
    
    public static void saveState(Bookstore bookstore) throws BookstoreException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(STATE_FILE))) {
            oos.writeObject(bookstore);
            System.out.println("Состояние успешно сохранено в файл: " + STATE_FILE);
        } catch (IOException e) {
            throw new BookstoreException("Ошибка сохранения состояния: " + e.getMessage(), e);
        }
    }
    
    public static Bookstore loadState() throws BookstoreException {
        File stateFile = new File(STATE_FILE);
        if (!stateFile.exists()) {
            System.out.println("Файл состояния не найден, будет создан новый магазин");
            return null;
        }
        
        if (stateFile.length() == 0) {
            System.out.println("Файл состояния пуст, будет создан новый магазин");
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(STATE_FILE))) {
            Bookstore bookstore = (Bookstore) ois.readObject();
            System.out.println("Состояние успешно загружено из файла: " + STATE_FILE);
            return bookstore;
        } catch (InvalidClassException e) {
            System.out.println("Версия классов изменилась, создается новый магазин");
            return null;
        } catch (StreamCorruptedException e) {
            stateFile.delete();
            throw new BookstoreException("Файл состояния поврежден. Создается новый магазин", e);
        } catch (IOException | ClassNotFoundException e) {
            throw new BookstoreException("Ошибка загрузки состояния: " + e.getMessage(), e);
        }
    }
    
    public static void deleteState() {
        File stateFile = new File(STATE_FILE);
        if (stateFile.exists()) {
            if (stateFile.delete()) {
                System.out.println("Файл состояния удален");
            } else {
                System.out.println("Не удалось удалить файл состояния");
            }
        }
    }
    
    public static void createBackup() throws BookstoreException {
        File stateFile = new File(STATE_FILE);
        if (!stateFile.exists()) {
            throw new BookstoreException("Нет файла состояния для создания резервной копии");
        }
        
        File backupFile = new File(STATE_FILE + ".backup");
        try (InputStream in = new FileInputStream(stateFile);
             OutputStream out = new FileOutputStream(backupFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            System.out.println("Резервная копия создана: " + backupFile.getName());
        } catch (IOException e) {
            throw new BookstoreException("Ошибка создания резервной копии: " + e.getMessage(), e);
        }
    }
    
    public static boolean restoreFromBackup() throws BookstoreException {
        File backupFile = new File(STATE_FILE + ".backup");
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
            System.out.println("Восстановлено из резервной копии");
            return true;
        } catch (IOException e) {
            throw new BookstoreException("Ошибка восстановления из резервной копии: " + e.getMessage(), e);
        }
    }
}
package service;

import di.annotation.Component;
import di.annotation.Inject;
import exception.BookstoreException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;

/**
 * Manages application state persistence.
 * Handles saving and loading of bookstore state.
 *
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class StateManager {

    /** State file name. */
    private static final String STATE_FILE = "bookstore_state.dat";

    /** Backup file name. */
    private static final String BACKUP_FILE = STATE_FILE + ".backup";

    /** Buffer size for file copy operations (1KB). */
    private static final int BUFFER_SIZE = 1024;

    /** The bookstore instance. */
    private final Bookstore bookstore;

    /**
     * Constructs a new StateManager with the specified bookstore.
     *
     * @param bookstore the bookstore instance
     */
    @Inject
    public StateManager(final Bookstore bookstore) {
        this.bookstore = bookstore;
    }

    /**
     * Saves the current state to file.
     *
     * @throws BookstoreException if save fails
     */
    public void saveState() throws BookstoreException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(STATE_FILE))) {
            oos.writeObject(bookstore);
        } catch (IOException e) {
            throw new BookstoreException(
                "Ошибка сохранения состояния: " + e.getMessage(), e);
        }
    }

    /**
     * Loads the state from file.
     *
     * @return the loaded bookstore or null if not found
     * @throws BookstoreException if load fails
     */
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
            throw new BookstoreException(
                "Ошибка загрузки состояния: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a backup of the current state.
     *
     * @throws BookstoreException if backup fails
     */
    public void createBackup() throws BookstoreException {
        File stateFile = new File(STATE_FILE);
        if (!stateFile.exists()) {
            throw new BookstoreException(
                "Нет файла состояния для создания резервной копии");
        }

        File backupFile = new File(BACKUP_FILE);
        copyFile(stateFile, backupFile);
    }

    /**
     * Restores state from backup.
     *
     * @return true if restore was successful
     * @throws BookstoreException if restore fails
     */
    public boolean restoreFromBackup() throws BookstoreException {
        File backupFile = new File(BACKUP_FILE);
        if (!backupFile.exists()) {
            throw new BookstoreException("Резервная копия не найдена");
        }

        File stateFile = new File(STATE_FILE);
        copyFile(backupFile, stateFile);
        return true;
    }

    /**
     * Copies a file from source to destination.
     *
     * @param source the source file
     * @param destination the destination file
     * @throws BookstoreException if copy fails
     */
    private void copyFile(final File source, final File destination)
            throws BookstoreException {
        byte[] buffer = new byte[BUFFER_SIZE];
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(destination)) {
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new BookstoreException(
                "Ошибка копирования файла: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes the state file.
     */
    public void deleteState() {
        File stateFile = new File(STATE_FILE);
        if (stateFile.exists()) {
            stateFile.delete();
        }
    }

    /**
     * Checks if state file exists and is not empty.
     *
     * @return true if state file exists and has content
     */
    public boolean stateFileExists() {
        File stateFile = new File(STATE_FILE);
        return stateFile.exists() && stateFile.length() > 0;
    }

    /**
     * Checks if backup file exists and is not empty.
     *
     * @return true if backup file exists and has content
     */
    public boolean backupFileExists() {
        File backupFile = new File(BACKUP_FILE);
        return backupFile.exists() && backupFile.length() > 0;
    }

    /**
     * Gets the size of the state file in bytes.
     *
     * @return the file size, or 0 if file doesn't exist
     */
    public long getStateFileSize() {
        File stateFile = new File(STATE_FILE);
        return stateFile.exists() ? stateFile.length() : 0;
    }

    /**
     * Gets the size of the backup file in bytes.
     *
     * @return the file size, or 0 if file doesn't exist
     */
    public long getBackupFileSize() {
        File backupFile = new File(BACKUP_FILE);
        return backupFile.exists() ? backupFile.length() : 0;
    }
}
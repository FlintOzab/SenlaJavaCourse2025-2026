package t4;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends Thread{
	
    private final int intervalSeconds;
    private volatile boolean running = true;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public Main(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
        setDaemon(true);
    }
    
    @Override
    public void run() {
        try {
            while (running) {
                String currentTime = LocalDateTime.now().format(formatter);
                System.out.println("Текущее время: " + currentTime);
                
                Thread.sleep(intervalSeconds * 1000);
            }
        } catch (InterruptedException e) {
            System.out.println("Поток прерван");
        }
    }
    
    public void stopThread() {
        running = false;
        interrupt();
    }
    
    public static void main(String[] args) throws InterruptedException {
        Main timeThread = new Main(3);
        timeThread.start();
        Thread.sleep(15000);
        timeThread.stopThread();
        System.out.println("Программа завершена");
    }

}

package t3;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Main {

    private static final int BUFFER_SIZE = 5;
    private static final Queue<Integer> buffer = new LinkedList<>();
    private static final Object lock = new Object();
    
    public static void main(String[] args) {
        Thread producer = new Thread(new Producer());
        Thread consumer = new Thread(new Consumer());
        producer.start();
        consumer.start();
    }
    
    static class Producer implements Runnable {
        private final Random random = new Random();
        
        @Override
        public void run() {
            try {
                while (true) {
                    int value = random.nextInt(100);
                    
                    synchronized (lock) {
                        while (buffer.size() == BUFFER_SIZE) {
                            System.out.println("Буфер полон. Производитель ждет");
                            lock.wait();
                        }
                        
                        buffer.add(value);
                        System.out.println("Производитель сгенерировал: " + value + " (Размер буфера: " + buffer.size() + ")");
                        
                        lock.notifyAll();
                        
                        Thread.sleep(random.nextInt(1000));
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    static class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (lock) {
                        while (buffer.isEmpty()) {
                            System.out.println("Буфер пуст. Потребитель ждет");
                            lock.wait();
                        }
                        
                        int value = buffer.poll();
                        System.out.println("Потребитель забрал: " + value + " (Размер буфера: " + buffer.size() + ")");
                        
                        lock.notifyAll();
                        
                        Thread.sleep(new Random().nextInt(1000));
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}

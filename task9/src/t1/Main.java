package t1;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final Object lock = new Object();
        Thread test = new Thread(() -> {
            for (int i = 0; i < 100000; i++)
            {}
        });
        
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
                synchronized (lock) {
                    Thread.sleep(100);
                }
                synchronized (lock) {
                    lock.wait(); 
                }
                
                Thread.sleep(50);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("1. Состояние после создания: " + thread.getState());
        thread.start();
        Thread.sleep(50);
        test.start();
        System.out.println("2. Состояние после start(): " + test.getState());
        Thread.sleep(20);
        System.out.println("3. Состояние во время sleep(): " + thread.getState());
        
        Thread blockingThread = new Thread(() -> {
            synchronized (lock) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        blockingThread.start();
        Thread.sleep(50); 
        System.out.println("4. Состояние при попытке входа в synchronized блок: " + thread.getState());
        Thread.sleep(1000);
        System.out.println("5. Состояние после вызова wait(): " + thread.getState());
       
        synchronized (lock) {
            lock.notify();
        }
        
        Thread.sleep(100);
        System.out.println("6. Состояние после завершения: " + thread.getState());
        
        thread.join();
        blockingThread.join();
    }
}
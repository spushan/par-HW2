import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Socks {

    public static void main(String[] args) {
        
        
        Random random = new Random();
        LinkedBlockingQueue<String> matchingQueue = new LinkedBlockingQueue<String>();
        LinkedBlockingQueue<String> washerQueue = new LinkedBlockingQueue<String>();
        LinkedBlockingQueue<String> nopairQueue = new LinkedBlockingQueue<String>();



        int red = random.nextInt(100) + 1;
        int green = random.nextInt(100) + 1;
        int blue = random.nextInt(100) + 1;
        int orange = random.nextInt(100) + 1;    
        AtomicInteger flag = new AtomicInteger(0);

        // class that creates the socks
        class SockMaker implements Runnable {
    
            private String color;
            private int number;
            
            public SockMaker (String color, int number) {
                this.color = color;
                this.number = number;
            }
        
            public void run(){
                try {
                    for (int i = 1; i < number; i++) {
                        System.out.println(color +" Sock: Produced " + i + " of " + number + color +  " Socks");
                        matchingQueue.put(color);
                    }
                    flag.incrementAndGet(); // increments the flag when the production is finish
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }

        // Sock making threads
        Thread redThread = new Thread(new SockMaker("Red", red));
        Thread greenThread = new Thread(new SockMaker("Green",green));
        Thread blueThread = new Thread(new SockMaker("Blue",blue));
        Thread orangeThread = new Thread(new SockMaker("Red",orange));
        
        redThread.start();
        greenThread.start();
        blueThread.start();
        orangeThread.start();
        
        //matching thread
        Thread matchingThread = new Thread(() -> {
            try {
                while (flag.get() < 4 || matchingQueue.size() != 0) {
                    String matchSocks = matchingQueue.take();
                    if(flag.get() < 4) {
                        if(matchingQueue.contains(matchSocks)) {
                            matchingQueue.remove(matchSocks);
                            washerQueue.put(matchSocks);
                            System.out.println("Send " + matchSocks + " to Washer. Total inside queue " + matchingQueue.size());
                        }
                        else {
                            matchingQueue.put(matchSocks);
                        }
                    }
                    else {
                        while (matchingQueue.size() != 0) {
                            if(matchingQueue.contains(matchSocks)) {
                                matchingQueue.remove(matchSocks);
                                washerQueue.put(matchSocks);
                                System.out.println("Send " + matchSocks + " to Washer. Total inside queue " + (matchingQueue.size() + nopairQueue.size()));
                            }
                            else {
                                nopairQueue.put(matchSocks);
                            }
                        flag.incrementAndGet();
                        }    
                    }
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        matchingThread.start();

        //washer thread
        Thread washerThread = new Thread(() -> {
             try {
                while(flag.get() < 5 || washerQueue.size() !=0) {
                    System.out.println(washerQueue.take() + " Destroyed");
                }
                System.out.println("DONE!!");
            }
            catch (InterruptedException e) {
                 e.printStackTrace();
            }
        });
        washerThread.start();
    }
}
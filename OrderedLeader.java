import java.util.concurrent.*;
import java.util.Random;

class OrderedLeader {

    volatile static String currentLeader;
    volatile static int currentRank = Integer.MIN_VALUE;
    volatile static String newOfficial;
    volatile static int newRank;
    private  static Object officialCreated = new Object();

    public static class Official implements Runnable {
        private String name;
        private int rank;
        private String leader;
        
        public Official(String name, int rank) {
            this.name = name;
            this.rank = rank;
            this.leader = name;
        }
        public int getRank() {
            return this.rank;
        }
        public String getLeader() {
            return this.leader;
        }
        public void setLeader(String leader) {
            this.leader = leader;
        }
        public void run() {
            newOfficial = this.name;
            newRank = this.rank;
            
            System.out.println("Thread Created: Name: "+ name + "\nThe leader is "+ leader);
            while(true) {
                synchronized (officialCreated) {
                    try{
                        officialCreated.wait();
                    }
                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            setLeader(currentLeader);
            }
        }
    }
    
    public static class Ranking implements Runnable {
        
        public void run() {
            try{
                Thread.sleep(Integer.MAX_VALUE);
            }
            catch (InterruptedException e) {
                while(true) {
                    if(newRank >= currentRank) {
                        synchronized (officialCreated) {
                            currentRank = newRank;
                            currentLeader = newOfficial;
                            officialCreated.notifyAll();
                        }
                        Thread.interrupted();
                    }
                    }     
                }
            }    
        }
    
    
    public static void main(String[] args) {
        
        Random random = new Random();
        Thread rankingThread = new Thread(new Ranking());
        rankingThread.start();
        
        for (int i = 0; i < 10; i++) {
            Thread temp = new Thread(new Official("Thread # "+i,random.nextInt(Integer.MAX_VALUE)));
            temp.start();
            rankingThread.interrupt();
        }
        System.out.println("The leader is "+ currentLeader);
    }
}
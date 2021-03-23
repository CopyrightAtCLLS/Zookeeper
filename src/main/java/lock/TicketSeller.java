package lock;

import org.apache.zookeeper.KeeperException;

import java.util.concurrent.TimeUnit;

public class TicketSeller {
    private void sell(){
        System.out.println("Started...");
        int sleepMillis=5000;
        try {
            TimeUnit.MILLISECONDS.sleep(sleepMillis);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Finished...");
    }

    private void sellWithLock() throws KeeperException, InterruptedException {
        MyLock lock=new MyLock();
        lock.acquireLock();
        sell();
        lock.releaseLock();
    }

    public static void main(String[] args) throws KeeperException, InterruptedException {
        TicketSeller ticketSeller = new TicketSeller();
        for (int i = 0; i < 10; i++) {
            ticketSeller.sellWithLock();
            System.out.println("--------------------------------------------------------");
        }
    }
}

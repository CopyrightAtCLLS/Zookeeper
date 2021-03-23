import org.omg.PortableServer.THREAD_POLICY_ID;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) throws InterruptedException, ClassNotFoundException {
        ArrayList<Object> list = new ArrayList<>();
        new Thread(() -> {
            synchronized (list) {
                try {
                    Thread.sleep(1000);
                    list.notifyAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A").start();
        synchronized (list) {
            System.out.println("before");
            list.wait();
            System.out.println("after");
        }
    }

}

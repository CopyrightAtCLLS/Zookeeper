package wathcer;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ZookeeperConnectionWatcher implements Watcher {
    static String IP = "localhost:2181";
    static ZooKeeper zooKeeper;
    static CountDownLatch countDownLatch = new CountDownLatch(1);


    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            if (watchedEvent.getType() == Event.EventType.None) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("Connection established...");
                    countDownLatch.countDown();
                } else if (watchedEvent.getState() == Event.KeeperState.Disconnected) {
                    System.out.println("Connection shutdown...");
                } else if (watchedEvent.getState() == Event.KeeperState.Expired) {
                    System.out.println("Session timeout...");
                    zooKeeper = new ZooKeeper(IP, 500, new ZookeeperConnectionWatcher());
                } else if (watchedEvent.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("Auth failed...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            zooKeeper = new ZooKeeper(IP, 500, new ZookeeperConnectionWatcher());
            countDownLatch.await();
            System.out.println("Session ID: "+zooKeeper.getSessionId());

//            zooKeeper.addAuthInfo("digest","clls:clls".getBytes());
            byte[] data = zooKeeper.getData("/node1", false, null);
            System.out.println("/node1 : "+new String(data));

            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            zooKeeper.close();
        }
    }
}

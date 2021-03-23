import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZookeeperConnection {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            final ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        System.out.println("Zookeeper Successfully Connected");
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
            System.out.println(zooKeeper.getSessionId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

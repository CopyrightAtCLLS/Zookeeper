package basicoperation;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZookeeperDelete {
    String IP = "localhost:2181";
    ZooKeeper zooKeeper;

    @Before
    public void before() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("Zookeeper Successfully Connected");
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
    }

    @After
    public void after() throws Exception {
        zooKeeper.close();
    }

    //同步
    @Test
    public void delete1() throws Exception {
        zooKeeper.delete("/delete/node1", -1);
    }

    //异步
    @Test
    public void delete2() throws Exception {
        zooKeeper.delete("/delete/node1", -1, new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(int i, String s, Object o) {
                System.out.println(i);
                System.out.println(s);
                System.out.println(o);
            }
        }, "Context");
        Thread.sleep(1000);
        System.out.println("Done");
    }
}

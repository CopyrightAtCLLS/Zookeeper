package basicoperation;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZookeeperExists {
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
    public void exists1() throws Exception {
        Stat stat = zooKeeper.exists("/exists", false);
        System.out.println(stat.getVersion());
    }

    //异步
    @Test
    public void exists2() throws Exception {
         zooKeeper.exists("/exists", false, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int i, String s, Object o, Stat stat) {
                System.out.println(i);
                System.out.println(s);
                System.out.println(stat.getVersion());
                System.out.println(o);
            }
        },"Context");
    }
}

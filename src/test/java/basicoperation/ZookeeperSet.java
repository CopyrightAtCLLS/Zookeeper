package basicoperation;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZookeeperSet {
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

    //同步插入
    @Test
    public void set1() throws KeeperException, InterruptedException {
        //version为-1表示不加乐观锁
        Stat stat = zooKeeper.setData("/set/node1", "node1".getBytes(), -1);
        System.out.println(stat.getVersion());
    }

    @Test
    public void set2() throws KeeperException, InterruptedException {
        //version为-1表示不CAS
        zooKeeper.setData("/set/node1", "node1".getBytes(), -1, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int i, String s, Object o, Stat stat) {
                System.out.println(i);
                System.out.println(s);
                System.out.println(stat.getVersion());
                System.out.println(o);
            }
        }, "Context");
        Thread.sleep(1000);
        System.out.println("Done");
    }
}

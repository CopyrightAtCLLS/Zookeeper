package basicoperation;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZookeeperGetChildren {
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
    public void get1() throws Exception {
        List<String> children = zooKeeper.getChildren("/get", false);
        System.out.println(children);
    }

    //异步
    @Test
    public void get2() throws Exception {
        zooKeeper.getChildren("/get", false, new AsyncCallback.ChildrenCallback() {
            @Override
            public void processResult(int i, String s, Object o, List<String> list) {
                System.out.println(i);
                System.out.println(s);
                System.out.println(list);
                System.out.println(o);
            }
        }, "Context");
        Thread.sleep(1000);
        System.out.println("Done");
    }
}

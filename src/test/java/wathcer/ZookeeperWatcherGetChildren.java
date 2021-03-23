package wathcer;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * 两种事件
 * 1、字节点变化(创建，删除)
 * 2、当前节点删除
 */
public class ZookeeperWatcherGetChildren {
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
                System.out.println("path = " + watchedEvent.getPath());
                System.out.println("eventType = " + watchedEvent.getType());
            }
        });
        countDownLatch.await();
    }

    @After
    public void after() throws Exception {
        zooKeeper.close();
    }

    @Test
    public void watcherGetChild1() throws KeeperException, InterruptedException {
        zooKeeper.getChildren("/watcher", true);
        Thread.sleep(100000);
        System.out.println("Done");
    }

}

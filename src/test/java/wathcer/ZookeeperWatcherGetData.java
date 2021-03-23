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
 * GetData可以捕获两类事件
 * 1、节点删除
 * 2、节点修改
 */
public class ZookeeperWatcherGetData {
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

    //使用连接监听器
    @Test
    public void watcherGetData1() throws KeeperException, InterruptedException {
        zooKeeper.getData("/watcher", true, null);
        Thread.sleep(100000);
        System.out.println("Done");
    }

    //自定义监听器
    @Test
    public void watcherGetData2() throws KeeperException, InterruptedException {
        zooKeeper.getData("/watcher", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("Custom Watcher...");
                System.out.println("path = " + watchedEvent.getPath());
                System.out.println("eventType = " + watchedEvent.getType());
            }
        }, null);
        Thread.sleep(100000);
        System.out.println("Done");
    }

    //复用
    @Test
    public void watcherGetData3() throws KeeperException, InterruptedException {
        zooKeeper.getData("/watcher", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    System.out.println("Custom Watcher...");
                    System.out.println("path = " + watchedEvent.getPath());
                    System.out.println("eventType = " + watchedEvent.getType());
                    if (watchedEvent.getType() == Event.EventType.NodeDataChanged)
                        zooKeeper.getData("/watcher", this, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
        Thread.sleep(100000);
        System.out.println("Done");
    }
}

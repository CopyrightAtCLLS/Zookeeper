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
 * Exists可以捕获三类事件
 * 1、节点创建
 * 2、节点删除
 * 3、节点修改
 */
public class ZookeeperWatcherExists {
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

    //使用连接对象的监视器
    @Test
    public void watcherExists1() throws KeeperException, InterruptedException {
        zooKeeper.exists("/watcher", true);
        Thread.sleep(100000);
        System.out.println("Done");
    }

    //自定义watcher
    @Test
    public void watcherExists2() throws KeeperException, InterruptedException {
        zooKeeper.exists("/watcher", (WatchedEvent watchedEvent) -> {
                    System.out.println("Custom Watcher...");
                    System.out.println("path = " + watchedEvent.getPath());
                    System.out.println("eventType = " + watchedEvent.getType());
                }
        );
        Thread.sleep(100000);
        System.out.println("Done");
    }

    //变一次性为持久
    @Test
    public void watcherExists3() throws KeeperException, InterruptedException {
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    System.out.println("Custom Watcher...");
                    System.out.println("path = " + watchedEvent.getPath());
                    System.out.println("eventType = " + watchedEvent.getType());
                    //通过该行代码
                    zooKeeper.exists("/watcher", this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        zooKeeper.exists("/watcher", watcher);
        Thread.sleep(100000);
        System.out.println("Done");
    }

    //多个监听器
    @Test
    public void watcherExists4() throws KeeperException, InterruptedException {
        zooKeeper.exists("/watcher", (WatchedEvent watchedEvent) -> {
                    System.out.println("Custom Watcher NO1...");
                    System.out.println("path = " + watchedEvent.getPath());
                    System.out.println("eventType = " + watchedEvent.getType());
                }
        );
        zooKeeper.exists("/watcher", (WatchedEvent watchedEvent) -> {
                    System.out.println("Custom Watcher NO2...");
                    System.out.println("path = " + watchedEvent.getPath());
                    System.out.println("eventType = " + watchedEvent.getType());
                }
        );
        Thread.sleep(100000);
        System.out.println("Done");
    }

}

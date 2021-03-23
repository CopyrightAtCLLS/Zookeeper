package basicoperation;

import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZookeeperGet {
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
    public void get1() throws Exception{
        Stat stat=new Stat();
        //bytes是返回的节点值
        byte[] bytes = zooKeeper.getData("/get/node1", false, stat);
        System.out.println(new String(bytes));
        System.out.println(stat.getVersion());
    }

    //异步
    @Test
    public void get2() throws Exception{
        zooKeeper.getData("/get/node1", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                System.out.println(i);
                System.out.println(s);
                System.out.println(new String(bytes));
                System.out.println(stat.getVersion());
                System.out.println(o);
            }
        },"Context");
        Thread.sleep(1000);
        System.out.println("Done");
    }
}

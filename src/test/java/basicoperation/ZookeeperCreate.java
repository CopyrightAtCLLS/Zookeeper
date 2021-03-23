package basicoperation;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZookeeperCreate {
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

    //world:anyone:crwda
    @Test
    public void create1() throws Exception {
        zooKeeper.create("/create/node1", "node1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    //world:anyone:r
    @Test
    public void create2() throws Exception {
        zooKeeper.create("/create/node1", "node1".getBytes(), ZooDefs.Ids.READ_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    //自定义world权限
    @Test
    public void create3() throws Exception {
        List<ACL> acls = new ArrayList<>();
        Id id = new Id("world", "anyone");
        acls.add(new ACL(ZooDefs.Perms.READ, id));
        acls.add(new ACL(ZooDefs.Perms.WRITE, id));
        zooKeeper.create("/create/node1", "node1".getBytes(), acls, CreateMode.PERSISTENT);
    }

    //自定义ip权限
    @Test
    public void create4() throws Exception {
        List<ACL> acls = new ArrayList<>();
        Id id = new Id("ip", "localhost");
        acls.add(new ACL(ZooDefs.Perms.ALL, id));
        zooKeeper.create("/create/node1", "node1".getBytes(), acls, CreateMode.PERSISTENT);
    }

    //auth权限
    @Test
    public void create5() throws Exception {
        zooKeeper.addAuthInfo("digest", "clls:clls".getBytes());
        zooKeeper.create("/create/node1", "node1".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
    }

    //auth权限
    @Test
    public void create6() throws Exception {
        zooKeeper.addAuthInfo("digest", "clls:clls".getBytes());
        List<ACL> acls = new ArrayList<>();
        Id id = new Id("auth", "clls");
        acls.add(new ACL(ZooDefs.Perms.READ, id));
        zooKeeper.create("/create/node1", "node1".getBytes(), acls, CreateMode.PERSISTENT);
    }

    //digest权限
    @Test
    public void create7() throws Exception {
        List<ACL> acls = new ArrayList<>();
        Id id = new Id("digest", "clls:V04RCGrQwBy1x1WtuVUJ9eSnYQA=");
        acls.add(new ACL(ZooDefs.Perms.ALL, id));
        zooKeeper.create("/create/node1", "node1".getBytes(), acls, CreateMode.PERSISTENT);
    }

    //顺序节点
    @Test
    public void create8() throws Exception {
        String result = zooKeeper.create("/create/node1", "node1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println(result);
    }

    //临时节点
    @Test
    public void create9() throws Exception {
        String result = zooKeeper.create("/create/node1", "node1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println(result);
    }

    //临时有序节点
    @Test
    public void create10() throws Exception {
        String result = zooKeeper.create("/create/node1", "node1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(result);
    }

    //异步创建节点
    @Test
    public void create11() throws Exception {
        zooKeeper.create("/create/node1",
                "node1".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,
                new AsyncCallback.StringCallback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx, String name) {
                        //0代表创建成功
                        System.out.println(rc);
                        System.out.println(path);
                        //
                        System.out.println(name);
                        System.out.println(ctx);
                    }
                },
                "I am context");
        Thread.sleep(10000);
        System.out.println("Done");
    }


}

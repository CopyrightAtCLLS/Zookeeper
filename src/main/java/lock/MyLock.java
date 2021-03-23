package lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MyLock {
    final String IP = "localhost:2181";
    CountDownLatch countDownLatch = new CountDownLatch(1);
    ZooKeeper zooKeeper;
    private static final String LOCK_ROOT_PATH = "/Locks";
    private static final String LOCK_NODE_NAME = "Lock_";
    private String lockPath;

    //监视上一个节点是否被删除
    Watcher watcher=new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
            if (watchedEvent.getType()== Event.EventType.NodeDeleted){
                synchronized (this){
                    notifyAll();
                }
            }
        }
    };

    public MyLock() {
        try {
            zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getType() == Event.EventType.None) {
                        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                            System.out.println("Connection Established...");
                            countDownLatch.countDown();
                        }
                    }
                }
            });
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acquireLock() throws KeeperException, InterruptedException {
        //创建锁节点
        createLock();
        //尝试获取锁
        tryLock();
    }

    public void createLock() throws KeeperException, InterruptedException {
        //创建locks
        Stat stat = zooKeeper.exists(LOCK_ROOT_PATH, false);
        if (stat == null) {
            zooKeeper.create(LOCK_ROOT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        //创建临时有序节点
        lockPath = zooKeeper.create(LOCK_ROOT_PATH + "/" + LOCK_NODE_NAME,
                new byte[0],
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("Node Successfully Created : " + lockPath);

    }

    public void tryLock() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren(LOCK_ROOT_PATH, false);
        Collections.sort(children);
        int index=children.indexOf(lockPath.substring(LOCK_ROOT_PATH.length()+1));
        if(index==0){
            System.out.println("Lock Obtained...");
            return;
        }else {
            //上一个节点的路径
            String path=children.get(index-1);
            Stat stat = zooKeeper.exists(LOCK_ROOT_PATH + "/" + path, watcher);
            //假如在设置监听器时，前一个锁已经释放的情况
            if(stat==null){
                tryLock();
            }else {
                synchronized (watcher){
                    watcher.wait();
                }
                tryLock();
            }
        }

    }

    public void releaseLock() throws KeeperException, InterruptedException {
        zooKeeper.delete(this.lockPath,-1);
        zooKeeper.close();
        System.out.println("Lock is released : "+this.lockPath);
    }

    public static void main(String[] args) {
        MyLock myLock = new MyLock();
        try {
            myLock.createLock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

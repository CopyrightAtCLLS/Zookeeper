package uniqueid;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

public class UniqueID implements Watcher {
    final String IP = "localhost:2181";
    CountDownLatch countDownLatch = new CountDownLatch(1);
    String defaultPath = "/uniqueID";
    ZooKeeper zooKeeper;

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            if (watchedEvent.getType() == Event.EventType.None) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("Connection Established...");
                    countDownLatch.countDown();
                } else if (watchedEvent.getState() == Event.KeeperState.Disconnected) {
                    System.out.println("Connection Shutdown...");
                } else if (watchedEvent.getState() == Event.KeeperState.Expired) {
                    System.out.println("Connection Timeout...");
                    zooKeeper = new ZooKeeper(IP, 5000, this);
                } else if (watchedEvent.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("Auth Failed...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UniqueID() {
        try {
            zooKeeper=new ZooKeeper(IP,5000,this);
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getUniqueID(){
        String path="";
        try {
            //创建临时有序节点
            path = zooKeeper.create(defaultPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        }catch (Exception e){
            e.printStackTrace();
        }
        return path.substring(9);
    }

    public static void main(String[] args) {
        UniqueID uniqueID = new UniqueID();
        for (int i = 0; i < 5; i++) {
            System.out.println(uniqueID.getUniqueID());
        }
    }
}

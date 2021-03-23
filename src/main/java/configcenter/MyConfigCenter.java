package configcenter;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class MyConfigCenter implements Watcher {
    final String IP = "localhost:2181";
    CountDownLatch countDownLatch = new CountDownLatch(1);
    static ZooKeeper zooKeeper;
    private String url;
    private String username;
    private String password;

    public static void main(String[] args) {
        try {
            MyConfigCenter configCenter = new MyConfigCenter();
            for (int i = 0; i < 10; i++) {
                Thread.sleep(3000);
                System.out.println("url : " + configCenter.getUrl() +
                        "\nusername : " + configCenter.getUsername() +
                        "\npassword : " + configCenter.getPassword());
                System.out.println("--------------------------------");
            }
        } catch (Exception e) {

        }
    }

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
                    zooKeeper = new ZooKeeper(IP, 5000,this);
                } else if (watchedEvent.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("Auth Failed...");
                }
            } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {//配置信息变化，重新加载
                initValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initValue() {
        try {
            this.url = new String(zooKeeper.getData("/config/url", true, null));
            this.username = new String(zooKeeper.getData("/config/username", true, null));
            this.password = new String(zooKeeper.getData("/config/password", true, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MyConfigCenter() throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper(IP, 5000, this);
        //等待连接创建
        countDownLatch.await();
        initValue();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

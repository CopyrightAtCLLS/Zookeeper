package curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorGet {
    String IP = "127.0.0.1:2181";
    CuratorFramework client;

    @Before
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy)
                .namespace("get")
                .build();
        client.start();
    }

    @After
    public void after() {
        client.close();
    }

    @Test
    public void get1() throws Exception {
        byte[] bytes = client.getData()
                .forPath("/node1");
        System.out.println(new String(bytes));
    }

    //读取节点属性
    @Test
    public void get2() throws Exception {
        Stat stat = new Stat();
        byte[] bytes = client.getData()
                .storingStatIn(stat)
                .forPath("/node1");
        System.out.println(new String(bytes));
        System.out.println(stat.getVersion());
    }

    //异步
    @Test
    public void get3() throws Exception {
        client.getData()
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        System.out.println(curatorEvent.getPath());
                        System.out.println(curatorEvent.getType());
                        System.out.println(new String(curatorEvent.getData()));
                    }
                })
                .forPath("/node1");
        Thread.sleep(100);
        System.out.println("Done...");
    }

}

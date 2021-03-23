package curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorSet {
    String IP = "127.0.0.1:2181";
    CuratorFramework client;

    @Before
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy)
                .namespace("set")
                .build();
        client.start();
    }

    @After
    public void after() {
        client.close();
    }

    @Test
    public void set1() throws Exception {
        client.setData()
                .forPath("/node1", "set".getBytes());
        System.out.println("Done...");
    }

    //版本Version
    @Test
    public void set2() throws Exception {
        client.setData()
                .withVersion(1)
                .forPath("/node1", "node2".getBytes());
    }

    //异步
    @Test
    public void set3() throws Exception {
        client.setData()
                .withVersion(-1)
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        System.out.println(curatorEvent.getPath());
                        System.out.println(curatorEvent.getType());
                    }
                })
                .forPath("/node1", "node3".getBytes());
        Thread.sleep(1000);
        System.out.println("Done");
    }
}

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

public class CuratorDelete {
    String IP = "127.0.0.1:2181";
    CuratorFramework client;

    @Before
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy)
                .namespace("delete")
                .build();
        client.start();
    }

    @After
    public void after() {
        client.close();
    }

    @Test
    public void delete1() throws Exception {
        client.delete().forPath("/node1");
        System.out.println("Done...");
    }

    @Test
    public void delete2() throws Exception {
        client.delete().withVersion(-1).forPath("/node1");
        System.out.println("Done...");
    }

    //deleteall
    @Test
    public void delete3() throws Exception {
        client.delete()
                .deletingChildrenIfNeeded()
                .withVersion(-1)
                .forPath("/node1");
        System.out.println("Done");
    }

    //异步
    @Test
    public void delete4() throws Exception {
        client.delete()
                .deletingChildrenIfNeeded()
                .withVersion(-1)
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        System.out.println(curatorEvent.getPath());
                        System.out.println(curatorEvent.getType());
                    }
                })
                .forPath("/node1");
        Thread.sleep(1000);
        System.out.println("Done");
    }
}

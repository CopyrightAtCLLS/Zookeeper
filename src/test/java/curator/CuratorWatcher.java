package curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;

public class CuratorWatcher {
    String IP = "127.0.0.1:2181";
    CuratorFramework client;

    @Before
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
    }

    @After
    public void after() {
        client.close();
    }

    @Test
    public void watcher1() throws Exception {
        NodeCache nodeCache = new NodeCache(client, "/watcher1");
        nodeCache.start();
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println(nodeCache.getCurrentData().getPath());
                System.out.println(new String(nodeCache.getCurrentData().getData()));
            }
        });
        Thread.sleep(1000);
        System.out.println("Done...");
        nodeCache.close();
    }

    @Test
    public void watcher2() throws Exception {
        //true,监视子节点变化并获取数据
        //false，监视子节点变化但是不获取数据
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/watcher1", true);
        pathChildrenCache.start();
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                System.out.println(pathChildrenCacheEvent.getType());
                System.out.println(pathChildrenCacheEvent.getData().getPath());
                System.out.println(new String(pathChildrenCacheEvent.getData().getData()));

            }
        });
        Thread.sleep(300000);
        System.out.println("Done...");
        pathChildrenCache.close();
    }
}

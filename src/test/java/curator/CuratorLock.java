package curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorLock {
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
    public void lock1() throws Exception {
        InterProcessLock interProcessLock = new InterProcessMutex(client, "/lock1");
        System.out.println("Waiting for lock...");
        interProcessLock.acquire();
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            System.out.println(i);
        }
        interProcessLock.release();
        System.out.println("Released");

    }

    @Test
    public void lock2() throws Exception {
        InterProcessReadWriteLock interProcessReadWriteLock = new InterProcessReadWriteLock(client, "/lock1");
        InterProcessMutex readLock = interProcessReadWriteLock.readLock();
        System.out.println("Waiting for lock...");
        readLock.acquire();
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            System.out.println(i);
        }
        readLock.release();
        System.out.println("Released");
    }

    @Test
    public void lock3() throws Exception {
        InterProcessReadWriteLock interProcessReadWriteLock = new InterProcessReadWriteLock(client, "/lock1");
        InterProcessMutex writeLock = interProcessReadWriteLock.writeLock();
        System.out.println("Waiting for lock...");
        writeLock.acquire();
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            System.out.println(i);
        }
        writeLock.release();
        System.out.println("Released");
    }
}

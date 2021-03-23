package curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.retry.RetryUntilElapsed;

public class CuratorConnection {
    public static void main(String[] args) {
        //Session重连策略
        /*
            3秒后重连一次，仅一次
            RetryPolicy retryPolicy=new RetryOneTime(3000);
         */
        /*
            每3秒一次，共三次
            RetryPolicy retryPolicy = new RetryNTimes(3, 3000);
         */
        /*
            每3秒一次，等待时间超过10s后停止重连
            RetryPolicy retryPolicy=new RetryUntilElapsed(10000,3000);
         */
        /*
            根据该公式计算:baseSleepTimeMs * Math.max(1,random.nextInt(1 << (retryCount + 1)))
            RetryPolicy retryPolicy=new ExponentialBackoffRetry(1000,3);
         */
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost")
                .sessionTimeoutMs(5000)
                //会话超时3s后,retry
                .retryPolicy(retryPolicy)
                //以create为父节点
                .namespace("create ")
                .build();
        client.start();
        System.out.println(client.isStarted());
        client.close();
    }
}

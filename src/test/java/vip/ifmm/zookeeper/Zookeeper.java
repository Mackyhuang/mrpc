package vip.ifmm.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/24 </p>
 */
public class Zookeeper {

    private static final Logger logger = LoggerFactory.getLogger(Zookeeper.class);

    private static final String ADDRESS = "47.106.128.206:2181";
    private static final int SESSION_TIMEOUT = 2000;
    ZooKeeper zooKeeper = null;
    CountDownLatch latch = new CountDownLatch(1);

    @Before
    public void connect(){

        try {
            zooKeeper = new ZooKeeper(ADDRESS, SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected){
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException e) {
            logger.error("", e);
        }
        catch (InterruptedException ex){
            logger.error("", ex);
        }
        logger.info("Zookeeper is already connected !");
    }

    @Test
    public void createNode(){
        try {
            zooKeeper.create("/Hello", "World".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("create node {} -> {}", "Hello", "World");
    }

    @Test
    public void watchNode(){
        List<String> children = null;
        try {
            children = zooKeeper.getChildren("/", new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    watchNode();
                }
            });
            Iterator<String> iterator = children.iterator();
            while (iterator.hasNext()){
                System.out.println(iterator.next());
            }
            TimeUnit.SECONDS.sleep(1000);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void exist(){
        try {
            Stat exist = zooKeeper.exists(ConnectProperties.ZOOKEEPER_BASE_PATH, false);
            System.out.println(exist);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

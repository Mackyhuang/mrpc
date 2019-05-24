package vip.ifmm.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/24 </p>
 */
public class ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private String zookeeperAddress = null;

    private volatile List<String> infoList = new ArrayList<>();

    ZooKeeper zooKeeper = null;

    //TODO 全局zookeeper
    public ServiceDiscovery(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;


        zooKeeper = connect();
        if (zooKeeper != null) {
            watchNode(zooKeeper);
        }
    }

    private ZooKeeper connect() {
        ZooKeeper zooKeeper = null;

        try {
            zooKeeper = new ZooKeeper(zookeeperAddress, ConnectProperties.ZOOKEEPER_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("", e);
        }
        return zooKeeper;
    }

    private void watchNode(ZooKeeper zooKeeper) {
        try {
            List<String> children = zooKeeper.getChildren(ConnectProperties.ZOOKEEPER_BASE_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    watchNode(zooKeeper);
                }
            });
            List<String> infoList = new ArrayList<>();
            Iterator<String> iterator = children.iterator();
            while (iterator.hasNext()) {
                //获取节点上的数据
                String curPath = ConnectProperties.ZOOKEEPER_BASE_PATH + "/" + iterator.next();
                byte[] data = zooKeeper.getData(curPath, false, null);
                infoList.add(new String(data));
            }
            LOGGER.debug("node data: {}", infoList);
            this.infoList = infoList;
            LOGGER.debug("Service discovery triggered updating connected server node.");
            UpdateConnectedServer();
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    public void UpdateConnectedServer(){
        //TODO
    }

    //负载
    public String discover() {
        String data = null;
        int size = infoList.size();
        if (size > 0) {
            if (size == 1) {
                data = infoList.get(0);
                LOGGER.debug("using only data: {}", data);
            } else {
                data = infoList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("using random data: {}", data);
            }
        }
        return data;
    }

    public void stop() {

        try {
            if (zooKeeper != null) {
                zooKeeper.close();
            }
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }

    }
}

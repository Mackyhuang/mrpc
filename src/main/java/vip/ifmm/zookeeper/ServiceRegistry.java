package vip.ifmm.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 服务注册类
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/24 </p>
 */
public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private String zookeeperAddress = null;

    public ServiceRegistry(String zookeeperAddress){
        this.zookeeperAddress = zookeeperAddress;
    }
    //TODO 全局zookeeper
    public void register(String nodeInfo){
        if (nodeInfo != null){
            ZooKeeper zooKeeper = connect();
            if (zooKeeper != null){
                checkRootNode(zooKeeper);
                createNode(zooKeeper, nodeInfo);
            }
        }
    }

    private ZooKeeper connect(){
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

    private void createNode(ZooKeeper zooKeeper, String nodeInfo){
        try {
            String createdNodeInfo = zooKeeper.create(
                    ConnectProperties.ZOOKEEPER_DATA_PATH,
                    nodeInfo.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.debug("创建zookeeper节点 ({} => {})", createdNodeInfo, nodeInfo);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    private void checkRootNode(ZooKeeper zooKeeper){
        try {
            Stat exist = zooKeeper.exists(ConnectProperties.ZOOKEEPER_BASE_PATH, false);
            if (exist == null){
                String root = zooKeeper.create(ConnectProperties.ZOOKEEPER_BASE_PATH,
                        new byte[0],
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
                LOGGER.debug("创建zookeeper父节点 ({})", root);
            }
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }

    }
}

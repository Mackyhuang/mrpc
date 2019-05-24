package vip.ifmm.zookeeper;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/24 </p>
 */
public interface ConnectProperties {

    //String ZOOKEEPER_ADDRESS = "47.106.128.206:2181";
    String ZOOKEEPER_BASE_PATH = "/rpc";
    String ZOOKEEPER_DATA_PATH = ZOOKEEPER_BASE_PATH + "/data";

    Integer ZOOKEEPER_SESSION_TIMEOUT = 2000;
}

package vip.ifmm.zookeeper;

import org.junit.Test;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/24 </p>
 */
public class DiscoveryTest {

    @Test
    public void watchTest(){
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery("47.106.128.206:2181");
    }
}

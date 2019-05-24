package vip.ifmm.zookeeper;

import org.junit.Test;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/24 </p>
 */
public class RegistryTest {

        @Test
        public void connectTest(){
                ServiceRegistry serviceRegistry = new ServiceRegistry("47.106.128.206:2181");
                serviceRegistry.register("127.0.0.1:8023");
        }
}

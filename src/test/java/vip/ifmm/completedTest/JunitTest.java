package vip.ifmm.completedTest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import vip.ifmm.client.RpcProxy;
import vip.ifmm.interfaces.Rpcinterface;
import vip.ifmm.zookeeper.ServiceDiscovery;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/25 </p>
 */
public class JunitTest {

    @Test
    public void server(){
        new ClassPathXmlApplicationContext("server-spring.xml");
    }

    @Test
    public void client(){
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery("47.106.128.206:2181");
        RpcProxy rpcProxy = new RpcProxy(serviceDiscovery);
        Rpcinterface rpcinterface = rpcProxy.create(Rpcinterface.class);
        int add = rpcinterface.add(1, 8);
        System.out.println("____________________");
        System.out.println(add);
    }
}

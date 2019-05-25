package vip.ifmm.completedTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import vip.ifmm.client.RpcProxy;
import vip.ifmm.interfaces.Rpcinterface;

import javax.annotation.Resource;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/25 </p>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:server-spring.xml",
        "classpath:client-spring.xml"
})
public class SpringTest {

    @Resource
    public RpcProxy rpcProxy;

    @Test
    public void rpcTest(){
        Rpcinterface rpcinterface = rpcProxy.create(Rpcinterface.class);
        int add = rpcinterface.add(1, 8);
        System.out.println(add);
    }
}

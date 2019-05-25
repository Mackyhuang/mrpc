package vip.ifmm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.ifmm.protocol.request.RpcRequestPacket;
import vip.ifmm.protocol.response.RpcResponsePacket;
import vip.ifmm.zookeeper.ServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * rpc代理类
 * 客户端调用这个办法，获取执行类方法的结果，使用动态代理使得rpc对于用户透明
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/25 </p>
 */
public class RpcProxy implements InvocationHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    private String rpcConnectAddress = null;
    private ServiceDiscovery discovery;

    public RpcProxy(ServiceDiscovery discovery){
        this.discovery = discovery;
    }

    public <T> T create(Class<T> interfaceClass){
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        RpcRequestPacket requestPacket = new RpcRequestPacket();
                        requestPacket.setRequestId(UUID.randomUUID().toString());
                        requestPacket.setClassName(method.getDeclaringClass().getName());
                        requestPacket.setMethodName(method.getName());
                        requestPacket.setParameterTypes(method.getParameterTypes());
                        requestPacket.setParameters(args);

                        if (rpcConnectAddress == null) {
                            rpcConnectAddress = discovery.discover();
                        }
                        String[] result = rpcConnectAddress.split(":");
                        if (result.length != 2) {
                            LOGGER.error("服务器入口地址错误 !");
                            return null;
                        }
                        String host = result[0];
                        int port = Integer.parseInt(result[1]);
                        //使用客户端向服务器发送rpc请求
                        RpcClient rpcClient = new RpcClient(host, port);
                        RpcResponsePacket response = rpcClient.send(requestPacket);

                        if (response.isSuccess()) {
                            return response.getResult();
                        } else {
                            throw response.getError();
                        }
                    }
                });
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequestPacket requestPacket = new RpcRequestPacket();
        requestPacket.setRequestId(UUID.randomUUID().toString());
        requestPacket.setClassName(method.getDeclaringClass().getName());
        requestPacket.setMethodName(method.getName());
        requestPacket.setParameterTypes(method.getParameterTypes());
        requestPacket.setParameters(args);

        if (rpcConnectAddress == null) {
            rpcConnectAddress = discovery.discover();
        }
        String[] result = rpcConnectAddress.split(":");
        if (result.length != 2) {
            LOGGER.error("服务器入口地址错误 !");
            return null;
        }
        String host = result[0];
        int port = Integer.parseInt(result[1]);
        //使用客户端向服务器发送rpc请求
        RpcClient rpcClient = new RpcClient(host, port);
        RpcResponsePacket response = rpcClient.send(requestPacket);

        if (response.isSuccess()) {
            return response.getResult();
        } else {
            throw response.getError();
        }
    }
}

package vip.ifmm.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import vip.ifmm.annotation.Expose;
import vip.ifmm.protocol.handler.PacketDecoder;
import vip.ifmm.protocol.handler.PacketEncoder;
import vip.ifmm.server.handler.RpcRequestHandler;
import vip.ifmm.zookeeper.ServiceRegistry;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * RPC服务端
 *
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/24 </p>
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    //PRC服务器地址入口
    private String serverEntrance = null;
    //PRC服务注册器
    private ServiceRegistry registry = null;

    private Map<String, Object> classRelationMap = new HashMap<>();

    public RpcServer(String serverEntrance, ServiceRegistry registry) {
        this.serverEntrance = serverEntrance;
        this.registry = registry;
    }

    /**
     * 实现ApplicationContextAware接口锁需要实现的类，在这里可以拿到spring的上下文，然后可以借此获得bean
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> interfaceList = applicationContext.getBeansWithAnnotation(Expose.class);
        LOGGER.info("服务端注册的接口: {}", interfaceList);
        if (MapUtils.isNotEmpty(interfaceList)) {
            Iterator<Object> iterator = interfaceList.values().iterator();
            while (iterator.hasNext()) {
                Object Implementation = iterator.next();
                String interfaceName = Implementation.getClass().getAnnotation(Expose.class).value().getName();
                classRelationMap.put(interfaceName, Implementation);
            }
        }
    }

    /**
     * 实现 InitializingBean 接口，通过这个方法可以在实例化这个类时执行这个方法
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(PacketDecoder.DECODER)
                                    .addLast(new RpcRequestHandler(classRelationMap))
                                    .addLast(PacketEncoder.ENCODER);
                        }
                    });

            String[] result = serverEntrance.split(":");
            if (result.length != 2) {
                LOGGER.error("服务器入口地址错误 !");
                return;
            }
            String host = result[0];
            int port = Integer.parseInt(result[1]);

            ChannelFuture future = bootstrap.bind(host, port).sync();
            LOGGER.debug("[" + host + ":" + port + "]服务绑定成功!");
            // 注册服务地址
            if (registry != null) {
                registry.register(serverEntrance);
            }
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

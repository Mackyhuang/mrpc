package vip.ifmm.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.ifmm.protocol.handler.PacketDecoder;
import vip.ifmm.protocol.handler.PacketEncoder;
import vip.ifmm.protocol.request.RpcRequestPacket;
import vip.ifmm.protocol.response.RpcResponsePacket;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Rpc远程调用的netty客户端 同时也是响应包的接收者，合在一起更容易的控制返回的结果
 *
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/25 </p>
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponsePacket> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private String host = null;
    private int port = 0;

//    private Lock lock = new ReentrantLock();
//    private Condition notReach = lock.newCondition();
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private RpcResponsePacket procedureResult = null;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 使用netty去服务器获取实现类的方法的调用结果
     * @param requestPacket 这次RPC调用的请求包
     * @return 包含结果的响应包
     * @throws InterruptedException
     */
    public RpcResponsePacket send(RpcRequestPacket requestPacket) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new PacketDecoder())
                                    .addLast(RpcClient.this)
                                    .addLast(new PacketEncoder());
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().writeAndFlush(requestPacket).sync();
            //等待服务器响应ChannelFutureListener.CLOSE
            //.addListener(ChannelFutureListener.CLOSE)这句执行，才能让future.channel().closeFuture().sync();继续运行
            countDownLatch.await();
            if(procedureResult != null){
                future.channel().closeFuture().sync();
            }
            return procedureResult;
        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponsePacket rpcResponsePacket) throws Exception {
        this.procedureResult = rpcResponsePacket;
        countDownLatch.countDown();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("客户端发生异常：", cause);
        ctx.close();
    }
}

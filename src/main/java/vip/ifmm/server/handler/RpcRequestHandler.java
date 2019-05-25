package vip.ifmm.server.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.ifmm.protocol.request.RpcRequestPacket;
import vip.ifmm.protocol.response.RpcResponsePacket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/25 </p>
 */
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequestPacket> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcRequestHandler.class);

    private Map<String, Object> classRelationMap = null;

    public RpcRequestHandler(Map<String, Object> classRelationMap) {
        this.classRelationMap = classRelationMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequestPacket rpcRequestPacket) throws Exception {
        RpcResponsePacket responsePacket = new RpcResponsePacket();
        responsePacket.setResponseId(rpcRequestPacket.getRequestId());
        try {
            Object result = procedureCall(rpcRequestPacket);
            responsePacket.setSuccess(true);
            responsePacket.setResult(result);
        } catch (Throwable throwable) {
            responsePacket.setSuccess(false);
            responsePacket.setError(throwable);
        }
        //.addListener(ChannelFutureListener.CLOSE)这句执行，才能让future.channel().closeFuture().sync();继续运行
        channelHandlerContext.channel().writeAndFlush(responsePacket).addListener(ChannelFutureListener.CLOSE);
    }

    private Object procedureCall(RpcRequestPacket rpcRequestPacket) throws InvocationTargetException {
        //获取实现类的Class信息
        String className = rpcRequestPacket.getClassName();
        Object implementation = classRelationMap.get(className);
        Class<?> implementationClass = implementation.getClass();
        //获取实现类内的方法名字
        String methodName = rpcRequestPacket.getMethodName();
        //获取方法的参数
        Object[] parameters = rpcRequestPacket.getParameters();
        //获取方法的参数类型
        Class<?>[] parameterTypes = rpcRequestPacket.getParameterTypes();

//        Method method = implementationClass.getMethod(methodName, parameterTypes);
//        method.setAccessible(true);
//        Object invoke = method.invoke(implementation, parameters);

        //通过cglib库的Fast API获取反射减少性能损失
        FastClass targetClass = FastClass.create(implementationClass);
        //获取目标方法
        FastMethod targetMethod = targetClass.getMethod(methodName, parameterTypes);
        Object result = targetMethod.invoke(implementation, parameters);
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("服务器发现异常：", cause);
        ctx.close();
    }
}

package vip.ifmm.protocol.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import vip.ifmm.protocol.PacketPicker;

/**
 * 基于长度域拆包器 处理粘包和半包问题
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/23 </p>
 */
public class Spliter extends LengthFieldBasedFrameDecoder {

    private static final Integer OFFSET = 7;
    private static final Integer LENGTH = 4;

    public Spliter() {
        super(Integer.MAX_VALUE, OFFSET, LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in.getInt(in.readerIndex()) != PacketPicker.MAGIC_NUM){
            ctx.channel().close();
            return null;
        }
        return super.decode(ctx, in);
    }
}

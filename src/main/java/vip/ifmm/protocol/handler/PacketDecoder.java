package vip.ifmm.protocol.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import vip.ifmm.protocol.Packet;
import vip.ifmm.protocol.PacketPicker;

import java.util.List;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/23 </p>
 */
@ChannelHandler.Sharable
public class PacketDecoder extends ByteToMessageDecoder {

    public static final PacketDecoder DECODER = new PacketDecoder();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        list.add(PacketPicker.PICKER.decode(byteBuf));
    }
}

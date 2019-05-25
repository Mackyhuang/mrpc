package vip.ifmm.protocol.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import vip.ifmm.protocol.Packet;
import vip.ifmm.protocol.PacketPicker;

import java.util.List;

/**
 * 服务端编解码器
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/26 </p>
 */
@ChannelHandler.Sharable
public class PacketCoder extends MessageToMessageCodec<ByteBuf, Packet> {

    public static final PacketCoder CODER = new PacketCoder();

    private PacketCoder(){

    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, List<Object> list) throws Exception {
        ByteBuf buffer = channelHandlerContext.channel().alloc().buffer();
        PacketPicker.PICKER.encode(buffer, packet);
        list.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        Packet reuslt = PacketPicker.PICKER.decode(byteBuf);
        list.add(reuslt);
    }
}

package vip.ifmm.protocol.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import vip.ifmm.protocol.Packet;
import vip.ifmm.protocol.PacketPicker;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/23 </p>
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {

    public static final PacketEncoder ENCODER = new PacketEncoder();
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        PacketPicker.PICKER.encode(byteBuf, packet);
    }
}

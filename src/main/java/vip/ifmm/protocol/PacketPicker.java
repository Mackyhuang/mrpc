package vip.ifmm.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import vip.ifmm.enums.PacketCommandTypeEnum;
import vip.ifmm.enums.SerializerTypeEnum;
import vip.ifmm.protocol.request.RpcRequestPacket;
import vip.ifmm.protocol.response.RpcResponsePacket;
import vip.ifmm.serializer.Serializer;
import vip.ifmm.serializer.impl.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/22 </p>
 */
public class PacketPicker {

    public static final PacketPicker PICKER = new PacketPicker();

    private Byte serializerType = 1;

    public static final Integer MAGIC_NUM = 0xaccababe;

    private static Map<Byte, Class<? extends Packet>> packetStore;

    private static Map<Byte, Serializer> serializerStore;

    {
        packetStore = new HashMap<>();
        packetStore.put(PacketCommandTypeEnum.RPC_REQUEST.getCode(), RpcRequestPacket.class);
        packetStore.put(PacketCommandTypeEnum.RPC_RESPONSE.getCode(), RpcResponsePacket.class);

        serializerStore = new HashMap<>();
        serializerStore.put(SerializerTypeEnum.JSON_SERIALIZER.getCode(), new JsonSerializer());
    }

    public void encode(ByteBuf byteBuf, Packet packet){
        Serializer serializer = serializerStore.get(serializerType);
        byte[] bytes = serializer.serialize(packet);
        byteBuf.writeInt(MAGIC_NUM);
        byteBuf.writeByte(packet.packetVersion);
        byteBuf.writeByte(serializerType);
        byteBuf.writeByte(packet.getCommandType());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public Packet decode(ByteBuf byteBuf){
        //跳过魔数
        byteBuf.skipBytes(4);
        //跳过版本号
        byteBuf.skipBytes(1);
        //当前包的序列化算法
        byte curPacketSerializerType = byteBuf.readByte();
        //当前包的指令类型
        byte curPacketCommandType = byteBuf.readByte();
        //数据包长度
        int len = byteBuf.readInt();
        //数据包
        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);

        Class<? extends Packet> clazz = packetStore.get(curPacketCommandType);
        Serializer serializer = serializerStore.get(curPacketSerializerType);
        if (clazz != null && serializer !=null){
            return serializer.deserialize(clazz, bytes);
        }
        return null;
    }
}

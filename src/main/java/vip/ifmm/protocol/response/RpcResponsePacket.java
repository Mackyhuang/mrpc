package vip.ifmm.protocol.response;

import vip.ifmm.enums.PacketCommandTypeEnum;
import vip.ifmm.protocol.Packet;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/22 </p>
 */
public class RpcResponsePacket extends Packet {

    @Override
    public Byte getCommandType() {
        return PacketCommandTypeEnum.RPC_RESPONSE.getCode();
    }
}

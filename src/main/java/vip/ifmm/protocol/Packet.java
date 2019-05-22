package vip.ifmm.protocol;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/22 </p>
 */
public abstract class Packet {

    Byte packetVersion = 1;

    public abstract Byte getCommandType();
}

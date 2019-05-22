package vip.ifmm.enums;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/23 </p>
 */
public enum  PacketCommandTypeEnum {

    RPC_REQUEST(0),
    RPC_RESPONSE(1);

    Byte code;

    PacketCommandTypeEnum(Integer code){
        this.code = (byte)(code & 0xFF);
    }

    public Byte getCode() {
        return code;
    }
}

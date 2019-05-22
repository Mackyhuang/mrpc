package vip.ifmm.enums;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/22 </p>
 */
public enum SerializerTypeEnum {

    JSON_SERIALIZER(1);

    Byte code;

    SerializerTypeEnum(Integer code){
        this.code = (byte)(code & 0xFF);
    }

    public Byte getCode() {
        return code;
    }
}

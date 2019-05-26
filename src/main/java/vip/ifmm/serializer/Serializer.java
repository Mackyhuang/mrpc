package vip.ifmm.serializer;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/22 </p>
 */
public interface Serializer {

    /**
     * 获取当前序列化器的类型
     * @return 序列化器的类型
     */
    byte getSerializerType();

    /**
     * 将对象序列化为字节数组
     * @param object 需要序列化的对象
     * @return 字节数组
     */
    <T> byte[] serialize(T object);

    /**
     * 将字节数组 反序列化为目标对象
     * @param clazz 目标对象的类信息
     * @param data 字节数组
     * @param <T> 目标对象的类类型
     * @return 目标对象
     */
    <T> T deserialize(Class<T> clazz, byte[] data);
}

package vip.ifmm.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import vip.ifmm.enums.SerializerTypeEnum;
import vip.ifmm.serializer.Serializer;

/**
 * @author: mackyhuang
 * <p>email: mackyhuang@163.com <p>
 * <p>date: 2019/5/22 </p>
 */
public class JsonSerializer implements Serializer {
    @Override
    public byte getSerializerType() {
        return SerializerTypeEnum.JSON_SERIALIZER.getCode();
    }

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] data) {
        return JSON.parseObject(data, clazz);
    }
}

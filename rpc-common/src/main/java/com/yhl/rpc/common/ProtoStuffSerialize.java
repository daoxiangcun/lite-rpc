package com.yhl.rpc.common;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by daoxiangcun on 17-7-31.
 */
public class ProtoStuffSerialize {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtoStuffSerialize.class);

    public static <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = RuntimeSchema.getSchema(cls);
            byte[] ret = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
            return ret;
        } catch (Exception e) {
            LOGGER.error("exception when serialize, obj:{}, {}", new Object[]{obj, e.getMessage(), e});
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> cls) {
        try {
            T message = cls.newInstance();
            Schema<T> schema = RuntimeSchema.getSchema(cls);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            LOGGER.error("exception when deserialize:{}", e.getMessage(), e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}

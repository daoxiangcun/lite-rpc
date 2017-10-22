package com.yhl.rpc.common;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

/**
 * Created by yuhongliang on 17-7-31.
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

    public static void main(String[] args) {
        String value = "datadata";
        RpcServerInfo serviceInfo = new RpcServerInfo();
        serviceInfo.setIp("11.22.33.44");
        serviceInfo.setPort("5678");
        byte[] data = ProtoStuffSerialize.serialize(serviceInfo);
//        for (int i = 0; i < data.length; i++) {
//            System.out.println("=============encode:===" + data[i]);
//        }
        RpcServiceResponse serviceResponse = new RpcServiceResponse();
        serviceResponse.setRequestId("11111");
        serviceResponse.setErrCode(Constants.ERR_CODE_OK);
        Map<String, String> result = Maps.newHashMap();
        result.put("xiaomiId", String.valueOf(3816960L));
        result.put("orderId", UUID.randomUUID().toString());
        result.put("time", String.valueOf(System.currentTimeMillis()));
        serviceResponse.setResponse(result);
        byte[] responseData = ProtoStuffSerialize.serialize(serviceResponse);
        System.out.println("=============responseData.length:===" + responseData.length);

        RpcServiceResponse desResponse = ProtoStuffSerialize.deserialize(responseData, RpcServiceResponse.class);
        System.out.println("=============desResponse :===" + desResponse + ", result:" + desResponse.getResponse());
    }
}

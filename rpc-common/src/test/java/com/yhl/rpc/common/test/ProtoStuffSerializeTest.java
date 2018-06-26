package com.yhl.rpc.common.test;

import com.google.common.collect.Maps;
import com.yhl.rpc.common.Constants;
import com.yhl.rpc.common.ProtoStuffSerialize;
import com.yhl.rpc.common.model.RpcServiceResponse;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

/**
 * Created by 44811 on 2018/7/28.
 */
public class ProtoStuffSerializeTest {
    @Test
    public void testSerialize() throws Exception {
        RpcServiceResponse serviceResponse = new RpcServiceResponse();
        serviceResponse.setRequestId("11111");
        serviceResponse.setErrCode(Constants.ERR_CODE_OK);
        Map<String, String> result = Maps.newHashMap();
        result.put("userId", String.valueOf(3816960L));
        result.put("orderId", UUID.randomUUID().toString());
        result.put("time", String.valueOf(System.currentTimeMillis()));
        serviceResponse.setResponse(result);
        byte[] responseData = ProtoStuffSerialize.serialize(serviceResponse);
        System.out.println("=============responseData.length:===" + responseData.length);
        Assert.assertNotNull(responseData);
    }

    @Test
    public void testDeserialize() throws Exception {
        RpcServiceResponse serviceResponse = new RpcServiceResponse();
        serviceResponse.setRequestId("11111");
        serviceResponse.setErrCode(Constants.ERR_CODE_OK);
        Map<String, String> result = Maps.newHashMap();
        result.put("userId", String.valueOf(3816960L));
        result.put("orderId", UUID.randomUUID().toString());
        result.put("time", String.valueOf(System.currentTimeMillis()));
        serviceResponse.setResponse(result);
        byte[] responseData = ProtoStuffSerialize.serialize(serviceResponse);
        System.out.println("=============responseData.length:===" + responseData.length);
        RpcServiceResponse desResponse = ProtoStuffSerialize.deserialize(responseData, RpcServiceResponse.class);
        System.out.println("=============desResponse :===" + desResponse + ", result:" + desResponse.getResponse());
        Assert.assertEquals(Constants.ERR_CODE_OK, desResponse.getErrCode());
        Map<String, String> respMap = (Map<String, String>)desResponse.getResponse();
        Assert.assertNotNull(respMap);
        String userId = respMap.get("userId");
        Assert.assertEquals("3816960", userId);
    }
}
package com.yhl.rpc.client;

import com.yhl.rpc.common.RpcServiceRequest;
import com.yhl.rpc.common.RpcServiceResponse;
import com.yhl.rpc.common.inf.IHelloService;

/**
 * Created by yuhongliang on 17-7-31.
 */
public class RpcHelloServiceProxy {
    public RpcHelloServiceProxy() {

    }

    public String callHello(String name) {
        RpcServiceRequest request = new RpcServiceRequest();
        request.setClassName(IHelloService.class.getName());
        request.setMethodName("hello");
        request.setParamTypes(new Class<?>[]{String.class});
        request.setParams(new Object[]{name});

        // 获取server信息,发送request
        RpcServiceResponse response = sendRequestAndWaitResponse(request);
        return response.getResponse().toString();
    }

    public String callTest() {
        RpcServiceRequest request = new RpcServiceRequest();
        request.setClassName(IHelloService.class.getName());
        request.setMethodName("test");
        request.setParamTypes(new Class<?>[]{Void.class});
        request.setParams(new Object[]{});

        // 获取server信息,发送request
        RpcServiceResponse response = sendRequestAndWaitResponse(request);
        return response.getResponse().toString();
    }

    private RpcServiceResponse sendRequestAndWaitResponse(RpcServiceRequest request) {
        return new RpcServiceResponse();
    }
}

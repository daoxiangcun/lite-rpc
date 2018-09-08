package com.yhl.rpc.server.serviceimpl;


import com.yhl.rpc.common.RpcService;
import com.yhl.rpc.inf.IHelloService;
import com.yhl.rpc.inf.model.User;
import com.yhl.rpc.server.servicehandler.AbstractServiceImplHandler;
import com.yhl.rpc.server.servicehandler.HelloServiceImplHandler;
import com.yhl.rpc.server.servicehandler.IRpcServiceHandler;

import java.util.Map;

/**
 * Created by daoxiangcun on 17-8-1.
 */
@RpcService
public class HelloServiceImpl implements IHelloService, IRpcServiceHandler {
    HelloServiceImplHandler helloHandler = new HelloServiceImplHandler(this);

    @Override
    public String hello(String name) {
        return "hello " + name;
    }

    @Override
    public int doubleInt(int data) {
        return data*2;
    }

    @Override
    public Integer doubleInteger(Integer data) {
        return data.intValue() * 2;
    }

    @Override
    public String helloUser(User user) {
        return "hello " + user.getName();
    }

    @Override
    public String getMapData(String key, Map<String, String> originMap) {
        if (originMap != null && originMap.containsKey(key)) {
            return originMap.get(key);
        }
        return "empty";
    }

    @Override
    public AbstractServiceImplHandler getServiceHandler() {
        return helloHandler;
    }
}

package com.yhl.rpc.server.servicehandler;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by yuhongliang on 17-11-8.
 */
@Service
public class ServiceImplHandlerManager {
    private HashMap<String, AbstractServiceImplHandler> serviceImplHandlerMap = Maps.newHashMap();

    @Autowired
    private HelloServiceImplHandler helloServiceImplHandler;

    @Autowired
    private UserInfoServiceImplHandler userInfoServiceImplHandler;

    public void init() {
        // 在map中加上接口的名称与接口处理类的映射
        serviceImplHandlerMap.put("com.yhl.rpc.common.inf.IHelloService", helloServiceImplHandler);
        serviceImplHandlerMap.put("com.yhl.rpc.common.inf.IUserInfoService", userInfoServiceImplHandler);
    }

    public Object handle(String interfaceName, String methodName, Class<?>[] paramTypes, Object[] params) throws Exception {
        AbstractServiceImplHandler handler = serviceImplHandlerMap.get(interfaceName);
        if (handler != null) {
            return handler.handle(methodName, paramTypes, params);
        }
        return null;
    }
}

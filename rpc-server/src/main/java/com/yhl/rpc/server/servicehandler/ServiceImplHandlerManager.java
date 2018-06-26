package com.yhl.rpc.server.servicehandler;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by daoxiangcun on 17-11-8.
 */
@Service
public class ServiceImplHandlerManager {
    ServiceImplHandlerRegistry registry;

    /**
     * 初始化，将RpcService对应的接口名与serivceHandler注册到registry上
     *
     * @param serviceMap
     */
    public void init(Map<String, Object> serviceMap) {
        registry = new ServiceImplHandlerRegistry();
        for (Map.Entry<String, Object> entry : serviceMap.entrySet()) {
            String infName = entry.getKey();
            Object serviceImpl = entry.getValue();
            if (serviceImpl instanceof IRpcServiceHandler) {
                registry.register(infName, ((IRpcServiceHandler) serviceImpl).getServiceHandler());
            }
        }
    }

    public Object handle(String interfaceName, String methodName, Class<?>[] paramTypes, Object[] params) throws Exception {
        AbstractServiceImplHandler handler = registry.getHandlerByInfFullName(interfaceName);
        if (handler != null) {
            return handler.handle(methodName, paramTypes, params);
        }
        return null;
    }
}

package com.yhl.rpc.server.servicehandler;

import com.yhl.rpc.common.model.MethodAndParamTypes;
import com.yhl.rpc.inf.IHelloService;
import com.yhl.rpc.inf.model.User;

import java.util.Map;

/**
 * Created by daoxiangcun on 17-11-8.
 */
public class HelloServiceImplHandler extends AbstractServiceImplHandler {
    IHelloService service;

    public HelloServiceImplHandler(IHelloService serviceImpl) {
        super(serviceImpl.getClass());
        this.service = serviceImpl;
    }

    @Override
    Object handle(MethodAndParamTypes method, Object[] params) throws Exception {
        if (method == null) {
            LOGGER.warn("method == null, params:{}", params);
            return null;
        }
        Object result;
        String methodName = method.getMethodName();
        if ("hello".equalsIgnoreCase(methodName)) {
            result = processHello(params);
        } else if ("doubleInt".equalsIgnoreCase(methodName)) {
            result = processDoubleInt(params);
        } else if ("helloUser".equalsIgnoreCase(methodName)) {
            result = processHelloUser(params);
        } else if ("helloUser".equalsIgnoreCase(methodName)) {
            result = processHelloUser(params);
        } else if ("getMapData".equalsIgnoreCase(methodName)) {
            result = processGetMapData(params);
        } else if ("doubleInteger".equalsIgnoreCase(methodName)) {
            result = processDoubleInteger(params);
        } else {
            String errMsg = "HelloServiceImplHandler, methodName of " + methodName + " not exist!";
            throw new Exception(errMsg);
        }
        return result;
    }

    private Object processHello(Object[] params) throws Exception {
        return service.hello((String) params[0]);
    }

    private Object processDoubleInt(Object[] params) throws Exception {
        Integer intObj = (Integer) params[0];
        return service.doubleInt(intObj.intValue());
    }

    private Object processHelloUser(Object[] params) throws Exception {
        return service.helloUser((User) params[0]);
    }

    private Object processGetMapData(Object[] params) throws Exception {
        return service.getMapData((String)params[0], (Map<String, String>)params[1]);
    }

    private Object processDoubleInteger(Object[] params) throws Exception {
        return service.doubleInteger((Integer) params[0]);
    }
}

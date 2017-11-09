package com.yhl.rpc.server.servicehandler;

import com.yhl.rpc.common.Constants;
import com.yhl.rpc.server.serviceimpl.HelloServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yuhongliang on 17-11-8.
 */
@Service
public class HelloServiceImplHandler extends AbstractServiceImplHandler {
    @Autowired
    HelloServiceImpl service;

    @Override
    Object handle(String methodName, String paramTypeStr, Object[] params) throws Exception {
        Object result = null;
        if ("hello".compareTo(methodName) == 0) {
            result = processHello(paramTypeStr, params);
        } else if ("doubleInt".compareTo(methodName) == 0) {
            result = processDoubleInt(paramTypeStr, params);
        } else {
            String errMsg = "HelloServiceImplHandler, methodName of " + methodName + " not exist!";
            throw new Exception(errMsg);
        }
        return result;
    }

    private Object processHello(String paramTypeStr, Object[] params) throws Exception {
        if (Constants.ClassType.STRING.compareTo(paramTypeStr) == 0) {
            return service.hello((String)params[0]);
        } else {
            String errMsg = "HelloServiceImplHandler, hello method paramTypeStr of " + paramTypeStr + " not exist!";
            throw new Exception(errMsg);
        }
    }

    private Object processDoubleInt(String paramTypeStr, Object[] params) throws Exception {
        if (Constants.ClassType.INTEGER.compareTo(paramTypeStr) == 0) {
            Integer intObj = (Integer)params[0];
            return service.doubleInt(intObj.intValue());
        } else {
            String errMsg = "HelloServiceImplHandler, doubleInt method paramTypeStr of " + paramTypeStr + " not exist!";
            throw new Exception(errMsg);
        }
    }
}

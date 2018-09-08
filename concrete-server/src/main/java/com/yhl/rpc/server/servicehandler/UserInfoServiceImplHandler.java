package com.yhl.rpc.server.servicehandler;

import com.yhl.rpc.common.model.MethodAndParamTypes;
import com.yhl.rpc.inf.IUserInfoService;
import org.springframework.stereotype.Service;

/**
 * Created by daoxiangcun on 17-11-8.
 */
@Service
public class UserInfoServiceImplHandler extends AbstractServiceImplHandler {
    IUserInfoService service;

    public UserInfoServiceImplHandler(IUserInfoService serviceImpl) {
        super(serviceImpl.getClass());
        this.service = serviceImpl;
    }

//    @Override
//    Object handle(String methodName, String paramTypeStr, Object[] params) throws Exception {
//        Object result = null;
//        if ("getProcessPayByProcessId".compareTo(methodName) == 0) {
//            result = processGetProcessPayByProcessId(paramTypeStr, params);
//        } else {
//            String errMsg = "UserInfoServiceImplHandler, methodName of " + methodName + " not exist!";
//            throw new Exception(errMsg);
//        }
//        return result;
//    }

    private Object processGetProcessPayByProcessId(String paramTypeStr, Object[] params) throws Exception {
        if ("String_String_Byte".compareTo(paramTypeStr) == 0) {
            return service.getProcessPayByProcessId((String)params[0], (String)params[1], (Byte)params[2]);
        } else if ("String_Long_String_Byte".compareTo(paramTypeStr) == 0) {
            return service.getProcessPayByProcessId((String)params[0], (Long)params[1], (String)params[2], (Byte)params[3]);
        } else {
            String errMsg = "UserInfoServiceImplHandler, getProcessPayByProcessId method paramTypeStr of " + paramTypeStr + " not exist!";
            throw new Exception(errMsg);
        }
    }

    @Override
    Object handle(MethodAndParamTypes method, Object[] params) throws Exception {
        if (method == null) {
            LOGGER.warn("method == null, params:{}", params);
            return null;
        }
//        Object result = null;
//        String methodName = method.getMethodName();
//        if ("hello".equalsIgnoreCase(methodName)) {
//            result = processHello(method.getParamTypes(), params);
//        } else if ("doubleInt".equalsIgnoreCase(methodName)) {
//            result = processDoubleInt(params);
//        } else if ("helloUser".equalsIgnoreCase(methodName)) {
//            result = processHelloUser(params);
//        } else {
//            String errMsg = "HelloServiceImplHandler, methodName of " + methodName + " not exist!";
//            throw new Exception(errMsg);
//        }
//        return result;
        return null;
    }
}

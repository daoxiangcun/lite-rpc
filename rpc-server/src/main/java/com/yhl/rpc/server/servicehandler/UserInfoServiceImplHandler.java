package com.yhl.rpc.server.servicehandler;

import com.yhl.rpc.server.serviceimpl.UserInfoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yuhongliang on 17-11-8.
 */
@Service
public class UserInfoServiceImplHandler extends AbstractServiceImplHandler {
    @Autowired
    UserInfoServiceImpl service;

    @Override
    Object handle(String methodName, String paramTypeStr, Object[] params) throws Exception {
        Object result = null;
        if ("getProcessPayByProcessId".compareTo(methodName) == 0) {
            result = processGetProcessPayByProcessId(paramTypeStr, params);
        } else {
            String errMsg = "UserInfoServiceImplHandler, methodName of " + methodName + " not exist!";
            throw new Exception(errMsg);
        }
        return result;
    }

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
}

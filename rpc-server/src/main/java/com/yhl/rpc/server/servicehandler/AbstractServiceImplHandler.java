package com.yhl.rpc.server.servicehandler;

import com.yhl.rpc.common.Constants;

/**
 * Created by yuhongliang on 17-11-8.
 */
public abstract class AbstractServiceImplHandler {
    Object handle(String methodName, Class<?>[] paramTypes, Object[] params) throws Exception {
        String paramTypeStr = getParamTypeStr(paramTypes);
        return handle(methodName, paramTypeStr, params);
    }

    abstract Object handle(String methodName, String paramTypeStr, Object[] params) throws Exception;

    private String getPrimitiveType(Class<?> paramType) {
        if (paramType == String.class) {
            return Constants.ClassType.STRING;
        } else if (paramType == Integer.class || paramType == Integer.TYPE) {
            return Constants.ClassType.INTEGER;
        } else if (paramType == Byte.class || paramType == Byte.TYPE) {
            return Constants.ClassType.BYTE;
        } else if (paramType == Long.class || paramType == Long.TYPE) {
            return Constants.ClassType.LONG;
        } else if (paramType == Double.class || paramType == Double.TYPE) {
            return Constants.ClassType.DOUBLE;
        } else if (paramType == Short.class || paramType == Short.TYPE) {
            return Constants.ClassType.SHORT;
        } else if (paramType == Float.class || paramType == Float.TYPE) {
            return Constants.ClassType.FLOAT;
        } else if (paramType == Boolean.class || paramType == Boolean.TYPE) {
            return Constants.ClassType.BOOLEAN;
        }
        return Constants.ClassType.VOID;
    }

    protected String getParamTypeStr(Class<?>[] paramTypes) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < paramTypes.length; i++) {
            String value = getPrimitiveType(paramTypes[i]);
            sb.append(value);
            if (i != paramTypes.length - 1) {
                sb.append("_");
            }
        }
        return sb.toString();
    }
}

package com.yhl.rpc.common.model;

/**
 * Created by yuhongliang on 18-7-25.
 */
public class MethodAndParamTypes {
    private String methodName;
    private Class<?>[] paramTypes;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }
}

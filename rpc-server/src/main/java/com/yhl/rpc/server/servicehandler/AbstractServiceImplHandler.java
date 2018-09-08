package com.yhl.rpc.server.servicehandler;

import com.google.common.collect.Lists;
import com.yhl.rpc.common.Constants;
import com.yhl.rpc.common.model.MethodAndParamTypes;
import com.yhl.rpc.common.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by daoxiangcun on 17-11-8.
 */
public abstract class AbstractServiceImplHandler {
    protected static Logger LOGGER = LoggerFactory.getLogger(AbstractServiceImplHandler.class);

    protected List<MethodAndParamTypes> methodAndParamTypesList = Lists.newArrayList();

    public AbstractServiceImplHandler(Class<?> interfaceClazz) {
        Method[] methods = interfaceClazz.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName();
            Class<?>[] paramTypes = method.getParameterTypes();
            MethodAndParamTypes methodAndParamTypes = new MethodAndParamTypes();
            methodAndParamTypes.setMethodName(name);
            methodAndParamTypes.setParamTypes(paramTypes);
            methodAndParamTypesList.add(methodAndParamTypes);
        }
    }

    /**
     * 找到合适的函数
     * @param methodName
     * @param paramTypes
     * @param params
     * @return
     */
    protected MethodAndParamTypes findMethodToCall(String methodName, Class<?>[] paramTypes, Object[] params) {
        MethodAndParamTypes targetMethodAndParamTypes = null;
        for (MethodAndParamTypes one : methodAndParamTypesList) {
            String name = one.getMethodName();
            if (!name.equals(methodName)) {
                continue;
            }
            // 方法名相同，检查参数类型
            Class<?>[] checkedParamTypes = one.getParamTypes();
            if (!paramTypeOk(checkedParamTypes, paramTypes, params)) {
                continue;
            }
            targetMethodAndParamTypes = one;
        }
        return targetMethodAndParamTypes;
    }

    /**
     * 判断paramTypes中的类型是否与checkedParamTypes类型一致
     * @param checkedParamTypes 方法中的各参数类型
     * @param paramTypes 传过来的参数类型
     * @return
     */
    protected boolean paramTypeOk(Class<?>[] checkedParamTypes, Class<?>[] paramTypes, Object[] params) {
        if (checkedParamTypes.length != paramTypes.length) {
            // 如果长度不相等，现在认为是无效的，跳过；后面可以针对这种：
            // 方法为：hello(int data1, String data2)
            // TODO 实际传过来的paramType为int，即少了一个String，这种也需要处理
            return false;
        }
        int size = checkedParamTypes.length;
        for (int i = 0; i < size; i++) {
            Class<?> checkedClazz = checkedParamTypes[i];
            Class<?> clazz = paramTypes[i];
            if (checkedClazz != clazz && !ClassUtils.isAssignable(checkedClazz, clazz)) {
                // 两个类型不同并且不能转换
                return false;
            }
        }
        return true;
    }

    Object handle(String methodName, Class<?>[] paramTypes, Object[] params) throws Exception {
        MethodAndParamTypes methodAndParamTypes = findMethodToCall(methodName, paramTypes, params);
        return handle(methodAndParamTypes, params);
    }

    abstract Object handle(MethodAndParamTypes method, Object[] params) throws Exception;

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

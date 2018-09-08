package com.yhl.rpc.server.servicehandler;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.HashMap;

/**
 * Created by daoxiangcun on 18-6-26.
 */
public class ServiceImplHandlerRegistry implements IServiceImplHandlerRegistry{
    private HashMap<String, AbstractServiceImplHandler> serviceImplHandlerMap = Maps.newHashMap();

    public void register(String infFullName, AbstractServiceImplHandler handler) {
        // 在map中加上接口的名称与接口处理类的映射
        serviceImplHandlerMap.put(infFullName, handler);
    }

    public AbstractServiceImplHandler getHandlerByInfFullName(String infFullName) {
        if (Strings.isNullOrEmpty(infFullName)) {
            return null;
        }
        return serviceImplHandlerMap.get(infFullName);
    }
}

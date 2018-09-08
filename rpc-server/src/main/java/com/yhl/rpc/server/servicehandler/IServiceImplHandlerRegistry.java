package com.yhl.rpc.server.servicehandler;

/**
 * Created by daoxiangcun on 18-6-26.
 */
public interface IServiceImplHandlerRegistry {
    void register(String infFullName, AbstractServiceImplHandler handler);
}

package com.yhl.rpc.server.serviceimpl;


import com.yhl.rpc.common.RpcService;
import com.yhl.rpc.common.inf.IHelloService;

/**
 * Created by yuhongliang on 17-8-1.
 */
@RpcService
public class HelloServiceImpl implements IHelloService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }

    @Override
    public int doubleInt(int data) {
        return data*2;
    }
}

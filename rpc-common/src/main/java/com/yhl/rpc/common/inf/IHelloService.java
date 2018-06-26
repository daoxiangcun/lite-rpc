package com.yhl.rpc.common.inf;

import com.yhl.rpc.common.model.User;

/**
 * Created by yuhongliang on 17-7-31.
 */
public interface IHelloService {
    String hello(String name);
    int doubleInt(int data);
    String helloUser(User user);
}

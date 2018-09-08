package com.yhl.rpc.inf;

import com.yhl.rpc.inf.model.User;

import java.util.Map;

/**
 * Created by daoxiangcun on 17-7-31.
 */
public interface IHelloService {
    String hello(String name);
    int doubleInt(int data);
    Integer doubleInteger(Integer data);
    String helloUser(User user);
    String getMapData(String key, Map<String, String> originMap);
}

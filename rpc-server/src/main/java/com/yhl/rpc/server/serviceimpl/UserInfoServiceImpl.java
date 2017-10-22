package com.yhl.rpc.server.serviceimpl;

import com.google.common.collect.Maps;
import com.yhl.rpc.common.RpcService;
import com.yhl.rpc.common.inf.IUserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by yuhongliang on 17-8-2.
 */
@RpcService
public class UserInfoServiceImpl implements IUserInfoService {
    private static Logger LOGGER = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    @Override
    public HashMap<String, String> getProcessPayByProcessId(String logId, long xiaomiId, String processKey, byte currency) {
        LOGGER.info("1111, logId:{}, xiaomiId:{}, processKey:{}, currency:{}", new Object[]{logId, xiaomiId, processKey, currency});
        HashMap<String, String> result = Maps.newHashMap();
        result.put("xiaomiId", String.valueOf(xiaomiId));
        result.put("orderId", UUID.randomUUID().toString());
        result.put("time", String.valueOf(System.currentTimeMillis()));
        return result;
    }

    @Override
    public HashMap<String, String> getProcessPayByProcessId(String logId, String processKey, byte currency) {
        LOGGER.info("2222, logId:{}, processKey:{}, currency:{}", new Object[]{logId, processKey, currency});
        HashMap<String, String> result = Maps.newHashMap();
        result.put("orderId", UUID.randomUUID().toString());
        result.put("time", String.valueOf(System.currentTimeMillis()));
        return result;
    }
}

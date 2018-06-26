package com.yhl.rpc.inf;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by daoxiangcun on 17-8-2.
 */
public interface IUserInfoService {
    HashMap<String,String> getProcessPayByProcessId(String logId, long xiaomiId, String processKey, byte currency);

    HashMap<String,String> getProcessPayByProcessId(String logId, String processKey, byte currency);
}

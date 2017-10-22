package com.yhl.rpc.common.inf;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuhongliang on 17-8-2.
 */
public interface IUserInfoService {
    HashMap<String,String> getProcessPayByProcessId(String logId, long xiaomiId, String processKey, byte currency);

    HashMap<String,String> getProcessPayByProcessId(String logId, String processKey, byte currency);
}

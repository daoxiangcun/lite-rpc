package com.yhl.rpc.common.inf;

import com.yhl.rpc.common.RpcServiceResponse;

/**
 * Created by yuhongliang on 17-8-7.
 */
public interface IRpcResponseListener{
    void onGetResponse(RpcServiceResponse response);
}

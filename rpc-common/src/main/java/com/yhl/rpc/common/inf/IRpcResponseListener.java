package com.yhl.rpc.common.inf;

import com.yhl.rpc.common.model.RpcServiceResponse;

/**
 * Created by daoxiangcun on 17-8-7.
 */
public interface IRpcResponseListener{
    void onGetResponse(RpcServiceResponse response);
}

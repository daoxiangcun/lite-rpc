package com.yhl.rpc.common;

/**
 * Created by Administrator on 2017/8/5.
 */
public class RpcException extends Exception {
    public RpcException(String errMsg) {
        super(errMsg);
    }

    public RpcException(Throwable e) {
        super(e);
    }
}

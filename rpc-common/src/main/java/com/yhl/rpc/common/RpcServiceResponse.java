package com.yhl.rpc.common;

/**
 * Created by yuhongliang on 17-7-31.
 */
public class RpcServiceResponse {
    private String requestId;
    private int errCode;
    private Object response;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}

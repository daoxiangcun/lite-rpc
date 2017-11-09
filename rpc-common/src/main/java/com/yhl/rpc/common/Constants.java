package com.yhl.rpc.common;

/**
 * Created by yuhongliang on 17-8-17.
 */
public interface Constants {
    int DEFAULT_TIMEOUT = 5000;

    int ERR_CODE_OK = 200;
    int ERR_CODE_CANCEL = 1001;
    int ERR_CODE_ERROR = 1002;

    String LOCAL_IP = "127.0.0.1";
    int RPC_SERVER_PORT = 9030;

    public interface ClassType {
        String VOID = "Void";
        String STRING = "String";
        String INTEGER = "Integer";
        String BYTE = "Byte";
        String LONG = "Long";
        String DOUBLE = "Double";
        String SHORT = "Short";
        String FLOAT = "Float";
        String BOOLEAN = "Boolean";
    }
}

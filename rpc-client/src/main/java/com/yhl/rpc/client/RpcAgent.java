package com.yhl.rpc.client;

import com.yhl.rpc.common.Constants;
import com.yhl.rpc.common.RpcFuture;
import com.yhl.rpc.common.RpcServerInfo;
import com.yhl.rpc.common.RpcServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;

public class RpcAgent {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcAgent.class);

    private RpcNettyClient client = null;

    private boolean connected = false;
    private RpcServerInfo serverInfo;

    public RpcAgent(String ip, String port) {
        init(ip, port);
        open();
    }

    public void init(String srvIp, String srvPort) {
        client = new RpcNettyClient(this);
        serverInfo = new RpcServerInfo();
        serverInfo.setIp(srvIp);
        serverInfo.setPort(srvPort);
    }

    private void open() {
        client.doOpen();
    }

    public void connect() {
        try {
            client.doConnect();
            if (client.isConnected()) {
                LOGGER.info("RpcAgent client is connected");
                connected = true;
            }
        } catch (TimeoutException e) {
            LOGGER.error("Connect to {}:{} timeout", serverInfo.getIp(), serverInfo.getPort());
        } catch (Throwable t) {
            LOGGER.error("Connect to {}:{} exception {}", new Object[]{serverInfo.getIp(), serverInfo.getPort(), t.getMessage(), t});
        }
    }

    public void close() {
        client.close();
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public Object syncSendRpcServiceRequest(RpcServiceRequest request, int timeout) throws Exception {
        LOGGER.info("RpcAgent.syncSendRpcServiceRequest, request:{}", request);
        RpcFuture future = client.rpcRequest(request);
        Object result = null;
        try {
            result = future.get(request.getRequestId(), timeout);
        } catch (Exception e) {
            LOGGER.info("future.get() exception, message is:{}", e.getMessage(), e);
        }
        return result;
    }

    public Object asyncSendRpcServiceRequest(RpcServiceRequest request) throws Exception {
        RpcFuture future = client.rpcRequest(request);
        return future;
    }
}

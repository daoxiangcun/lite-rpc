package com.yhl.rpc.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yhl.rpc.common.RpcException;
import com.yhl.rpc.common.RpcFuture;
import com.yhl.rpc.common.RpcServerInfo;
import com.yhl.rpc.common.RpcServiceRequest;
import com.yhl.rpc.common.RpcServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class RpcAgentPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcAgentPool.class);
    private Cache<String, RpcAgent> rpcClientCache;

    @PostConstruct
    public void init() {
        rpcClientCache = CacheBuilder.newBuilder().build();
    }

    public RpcAgent getOneClientByServerIpPort(String ip, String port) {
        LOGGER.info("getOneClientByIpPort, ip:{}, port:{}", ip, port);
        String ipPort = ip + ":" + port;
        synchronized (rpcClientCache) {
            RpcAgent client = rpcClientCache.getIfPresent(ipPort);
            if (client == null || !client.isConnected()) {
                if (client != null && !client.isConnected()) {
                    LOGGER.error("getOneClientByIpPort, ip:{}, port:{}, client.isConnected:{}", new Object[]{ip, port, client.isConnected()});
                }
                rpcClientCache.invalidate(ipPort);
                client = new RpcAgent(ip, port);
                boolean success = openAndConnect(client);
                if (success) {
                    rpcClientCache.put(ipPort, client);
                    return client;
                } else {
                    LOGGER.error("openAndConnect fail, can not connect to ip {} port {}", ip, port);
                    return null;
                }
            } else {
                LOGGER.info("getOneClientByIpPort, reuse client");
                return client;
            }
        }
    }

    private boolean openAndConnect(RpcAgent client) {
        int tryTimes = 0;
        while(!client.isConnected()) {
            client.connect();
            if (tryTimes > 3) {
                return false;
            }
            if (tryTimes > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.error("exception {}", e.getMessage(), e);
                }
            }
            tryTimes++;
        }
        return true;
    }

    public void close() {
        for (Map.Entry<String, RpcAgent> entry : rpcClientCache.asMap().entrySet()) {
            RpcAgent client = entry.getValue();
            if (client != null && client.isConnected()) {
                client.close();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LOGGER.error("RpcAgentPool close exception, {}", e.getMessage(), e);
            }
        }
        rpcClientCache.invalidateAll();
        rpcClientCache.cleanUp();
    }

    public RpcServiceResponse sendRequestAndWaitResponse(RpcServerInfo serverInfo, RpcServiceRequest request, int timeout) throws RpcException {
        RpcAgent client = getOneClientByServerIpPort(serverInfo.getIp(), serverInfo.getPort());
        if (client == null) {
            LOGGER.error("get client by ip {} and port {} failed, client is null", serverInfo.getIp(), serverInfo.getPort());
            String errMsg = "get client by ip " + serverInfo.getIp() + " and port " + serverInfo.getPort() + " failed";
            throw new RpcException(errMsg);
        }
        try {
            return (RpcServiceResponse)client.syncSendRpcServiceRequest(request, timeout);
        } catch (Exception e) {
            LOGGER.error("exception happen:{}", e.getMessage(), e);
            throw new RpcException(e);
        }
    }

    public RpcFuture sendRequestAsync(RpcServerInfo serviceInfo, RpcServiceRequest request) {
        RpcAgent client = getOneClientByServerIpPort(serviceInfo.getIp(), serviceInfo.getPort());
        try {
            return (RpcFuture) client.asyncSendRpcServiceRequest(request);
        } catch (Exception e) {
            LOGGER.error("async send exception happen:{}", e.getMessage(), e);
            return null;
        }
    }
}

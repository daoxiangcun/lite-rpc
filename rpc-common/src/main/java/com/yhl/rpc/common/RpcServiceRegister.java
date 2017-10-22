package com.yhl.rpc.common;

import org.slf4j.LoggerFactory;

/**
 * 注册服务到zk上
 * Created by yuhongliang on 17-7-31.
 */
public class RpcServiceRegister {
    private static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RpcServiceRegister.class);
    private String ZK_RPC_ROOT = "/rpcservices";
    private ZookeeperUtils zkUtils;
    public RpcServiceRegister() {
        zkUtils = new ZookeeperUtils("staging");
    }

    public void registerToZk(String ip, String port, String serviceName) {
        String serviceRootPath = ZK_RPC_ROOT;
        if (!zkUtils.getZkClient().exists(serviceRootPath)) {
            zkUtils.createPersistent(serviceRootPath);
            LOGGER.debug("create service root node: {}", serviceRootPath);
        }

        String servicePath = serviceRootPath + "/" + serviceName;
        if (!zkUtils.getZkClient().exists(servicePath)) {
            zkUtils.createPersistent(servicePath);
            LOGGER.debug("create service node: {}", servicePath);
        }

        String poolPath = servicePath + "/pool";
        if (!zkUtils.getZkClient().exists(poolPath)) {
            zkUtils.createPersistent(poolPath);
            LOGGER.debug("create service pool node: {}", poolPath);
        }

        String ipPort = ip + ":" + port;
        String ipPortPath = poolPath + "/" + ipPort;
        zkUtils.createEphemeral(ipPortPath, "");
        LOGGER.debug("create node: {}, value empty", ipPortPath);
    }
}

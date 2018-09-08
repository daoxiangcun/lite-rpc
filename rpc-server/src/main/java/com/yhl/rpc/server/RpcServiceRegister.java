package com.yhl.rpc.server;

import com.yhl.rpc.common.utils.ZookeeperSupport;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 注册服务到zk上
 * Created by daoxiangcun on 17-7-31.
 */
public class RpcServiceRegister {
    private static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RpcServiceRegister.class);
    private String ZK_RPC_ROOT = "/rpcservices";
    private ZookeeperSupport zkSupport;

    public RpcServiceRegister() {
        String servers = loadZkConfig();
        zkSupport = new ZookeeperSupport(servers);
    }

    private String loadZkConfig() {
        Properties properties = System.getProperties();
        try {
            Resource res = new ClassPathResource("zookeeper.properties");
            InputStream in = res.getInputStream();
            properties.load(in);
            in.close();
            return (String) properties.get("host");
        } catch (IOException e) {
            LOGGER.error("zookeeper.properties file init fail:{}", e.getMessage(), e);
        }
        return null;
    }

    public void registerToZk(String ip, String port, String serviceName) {
        String serviceRootPath = ZK_RPC_ROOT;
        if (!zkSupport.exists(serviceRootPath)) {
            zkSupport.createPersistent(serviceRootPath);
            LOGGER.debug("create service root node: {}", serviceRootPath);
        }

        String servicePath = serviceRootPath + "/" + serviceName;
        if (!zkSupport.exists(servicePath)) {
            zkSupport.createPersistent(servicePath);
            LOGGER.debug("create service node: {}", servicePath);
        }

        String poolPath = servicePath + "/pool";
        if (!zkSupport.exists(poolPath)) {
            zkSupport.createPersistent(poolPath);
            LOGGER.debug("create service pool node: {}", poolPath);
        }

        String ipPort = ip + ":" + port;
        String ipPortPath = poolPath + "/" + ipPort;
        zkSupport.createEphemeral(ipPortPath, "");
        LOGGER.debug("create node: {}, value empty", ipPortPath);
    }
}

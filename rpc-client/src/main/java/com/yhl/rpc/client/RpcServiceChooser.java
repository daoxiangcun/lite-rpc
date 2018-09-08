package com.yhl.rpc.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yhl.rpc.common.inf.IZKChildListener;
import com.yhl.rpc.common.model.RpcServerInfo;
import com.yhl.rpc.common.utils.ZookeeperSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * 从zk上获取提供服务的ip和port
 * zk上路径类似:
 * /rpcservices/com.yhl.xx.xx.service.xxService
 * Created by daoxiangcun on 17-7-31.
 */
@Service
public class RpcServiceChooser {
    private static Logger LOGGER = LoggerFactory.getLogger(RpcServiceChooser.class);
    private ZookeeperSupport zkSupport;
    private Map<String, List<RpcServerInfo>> serverMap = Maps.newHashMap();
    private static final String ZK_RPC_ROOT = "/rpcservices";

    @PostConstruct
    public void init() {
        LOGGER.info("RpcServiceChooser init");
        String zkServers = loadZkConfig();
        zkSupport = new ZookeeperSupport(zkServers);
        serverMap = listAllServicesFromZk();
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

    private Map<String, List<RpcServerInfo>> listAllServicesFromZk() {
        serverMap.clear();
        List<String> children = zkSupport.getChildrenNames(ZK_RPC_ROOT);
        LOGGER.info("children is:{}", children);
        if (children != null && children.size() > 0) {
            for (String one : children) {
                final String servicePath = ZK_RPC_ROOT + "/" + one;
                LOGGER.info("servicePath={}", servicePath);
                List<RpcServerInfo> serverList = listServersFromZkOfService(servicePath);
                serverMap.put(one, serverList);
                zkSupport.registerChildChanges(servicePath, new IZKChildListener() {
                    @Override
                    public void onChanged(String parentPath, List<String> currentChildren) {
                        List<RpcServerInfo> rpcServiceInfoList = listServersFromZkOfService(servicePath);
                        serverMap.put(servicePath, rpcServiceInfoList);
                    }
                });
            }
        }
        return serverMap;
    }

    private List<RpcServerInfo> listServersFromZkOfService(String servicePath) {
        List<RpcServerInfo> serverList = Lists.newArrayList();
        String poolPath = servicePath + "/pool";
        List<String> children = zkSupport.getChildrenNames(poolPath);
        if (children != null && children.size() > 0) {
            for (String one : children) {
                String ip = one.split(":")[0];
                String port = one.split(":")[1];
                RpcServerInfo info = new RpcServerInfo();
                info.setIp(ip);
                info.setPort(port);
                serverList.add(info);
            }
        }
        return serverList;
    }

    public RpcServerInfo chooseOneServer(String serviceName) {
        List<RpcServerInfo> serviceInfoList = serverMap.get(serviceName);
        LOGGER.info("serviceName:{}, serverList size is {}", serviceName, serviceInfoList.size());
        if (serviceInfoList.size() == 0) {
            return null;
        }
        Random random = new Random();
        int serverNo = random.nextInt(serviceInfoList.size());
        return serviceInfoList.get(serverNo);
    }
}

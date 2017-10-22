package com.yhl.rpc.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.miliao.zookeeper.ZKChildListener;
import com.yhl.rpc.common.RpcServerInfo;
import com.yhl.rpc.common.ZookeeperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 从zk上获取提供服务的ip和port
 * zk上路径类似:
 *          /rpcservices/com.xiaomi.miui.mibi.service.MibiService
 * Created by yuhongliang on 17-7-31.
 */
@Service
public class RpcServiceChooser {
    private static Logger LOGGER = LoggerFactory.getLogger(RpcServiceChooser.class);
    private ZookeeperUtils zkUtils;
    private Map<String, List<RpcServerInfo>> serverMap = Maps.newHashMap();
    private static final String ZK_RPC_ROOT = "/rpcservices";

    @PostConstruct
    public void init() {
        LOGGER.info("RpcService init");
        zkUtils = new ZookeeperUtils("staging");
        serverMap = listAllServicesFromZk();
    }

    private Map<String, List<RpcServerInfo>> listAllServicesFromZk() {
        serverMap.clear();
        List<String> children = zkUtils.getZkClient().getChildrenNames(ZK_RPC_ROOT);
        LOGGER.info("children is:{}", children);
        if (children != null && children.size() > 0) {
            for(String one:children) {
                final String servicePath = ZK_RPC_ROOT + "/" + one;
                LOGGER.info("servicePath={}", servicePath);
                List<RpcServerInfo> serverList = listServersFromZkOfService(servicePath);
                serverMap.put(one, serverList);
                zkUtils.getZkClient().registerChildChanges(servicePath, new ZKChildListener() {
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
        List<String> children = zkUtils.getZkClient().getChildrenNames(poolPath);
        if (children != null && children.size() > 0) {
            for(String one:children) {
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

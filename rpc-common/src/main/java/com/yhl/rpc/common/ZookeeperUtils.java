package com.yhl.rpc.common;

import com.xiaomi.miliao.zookeeper.EnvironmentType;
import com.xiaomi.miliao.zookeeper.ZKClient;
import com.xiaomi.miliao.zookeeper.ZKFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(ZookeeperUtils.class);

    private ZKClient zkClient ;

    public ZookeeperUtils() {
        zkClient = ZKFacade.getClient();
    }

    public ZKClient getZkClient() {
        return zkClient;
    }

    public void setZkClient(ZKClient zkClient) {
        this.zkClient = zkClient;
    }

    public ZookeeperUtils(String environment) {
        zkClient = ZKFacade.getClient(EnvironmentType.valueOf(environment.toUpperCase()));
    }

    public String getData(String path) {
        String data = null;
        try {
            data = zkClient.getData(String.class, path);
        } catch (Exception e) {
            LOGGER.warn("Exception when get data from zookeeper", e);
        }
        return data;
    }

    public void createEphemeral(String path, String data) {
        zkClient.createEphemeral(path, data);
    }

    public void createPersistent(String path) {
        zkClient.createPersistent(path);
    }
}

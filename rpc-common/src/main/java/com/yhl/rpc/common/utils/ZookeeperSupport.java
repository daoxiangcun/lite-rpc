package com.yhl.rpc.common.utils;

import com.google.common.base.Strings;
import com.yhl.rpc.common.inf.IZKChildListener;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by yuhongliang on 18-8-24.
 */
public class ZookeeperSupport {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public static final int SESSION_TIMEOUT = 30000;
    public static final int CONNECTION_TIMEOUT = 30000;
    private static final String DEFAULT_ZK_SERVERS = "127.0.0.1:2181";
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static ZkClient client = null;
    private String zkServers = DEFAULT_ZK_SERVERS;

    public ZookeeperSupport(String servers) {
        if (!Strings.isNullOrEmpty(servers)) {
            zkServers = servers;
        }
    }

    public ZookeeperSupport() {
    }

    private ZkClient getClient() {
        synchronized (this) {
            if (client == null) {
                synchronized (this) {
                    client = new ZkClient(zkServers, SESSION_TIMEOUT, CONNECTION_TIMEOUT, new BytesPushThroughSerializer());
                }
            }
        }
        return client;
    }

    public boolean exists(String path) {
        return getClient().exists(path);
    }

    public boolean delete(String path) {
        return getClient().delete(path);
    }

    public String getData(String path) {
        byte[] bytes = getClient().readData(path);
        return new String(bytes, DEFAULT_CHARSET);
    }

    public void createEphemeral(String path, String data) {
        String realPath = path;
        if (exists(realPath)) {
            delete(realPath);
        }
        Object resultData = data.getBytes(DEFAULT_CHARSET);
        getClient().createEphemeral(path, resultData);
    }

    public List<String> getChildrenNames(final String path) {
        return getClient().getChildren(path);
    }

    public void registerChildChanges(final String path, final IZKChildListener listener) {
        String realPath = path;
        final IZkChildListener underlyingListener = new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) {
                listener.onChanged(parentPath, currentChilds);
            }
        };
        getClient().subscribeChildChanges(realPath, underlyingListener);
    }

    public void createPersistent(String path) {
        getClient().createPersistent(path, false);
    }

    public void createPersistent(String path, String data) {
        Object resultData = data.getBytes(DEFAULT_CHARSET);
        getClient().createPersistent(path, resultData);
    }
}

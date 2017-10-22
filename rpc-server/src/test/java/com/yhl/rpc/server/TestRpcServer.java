package com.yhl.rpc.server;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yuhongliang on 17-8-1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath*:applicationContext_test.xml"})
public class TestRpcServer {
    protected Logger LOGGER = LoggerFactory.getLogger(TestRpcServer.class);

    @Autowired
    RpcNettyServer rpcNettyServer;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("zookeeper.host", "staging");
        //BasicConfigurator.configure();
    }

    @Test
    public void testServe() throws InterruptedException {
        long count = 0;
        while(true) {
            Thread.sleep(1000);
            LOGGER.info("==== count= " + count);
            count++;
        }
    }
}

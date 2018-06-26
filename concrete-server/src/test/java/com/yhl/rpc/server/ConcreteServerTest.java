package com.yhl.rpc.server;

import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by daoxiangcun on 17-8-1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath*:applicationContext_test.xml"})
public class ConcreteServerTest {
    @Autowired
    RpcNettyServer rpcNettyServer;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("zookeeper.host", "staging");
        BasicConfigurator.configure();
    }

    @Test
    public void testStartServe() throws InterruptedException {
        long count = 0;
        while (true) {
            Thread.sleep(1000);
            count++;
        }
    }
}

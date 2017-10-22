package com.yhl.rpc.client;

import com.yhl.rpc.common.RpcException;
import com.yhl.rpc.common.inf.IHelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.CountDownLatch;

/**
 * Created by yuhongliang on 17-8-14.
 */
@RunWith(JUnit4Log4jClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml"})
public class TestRpcPress extends BaseRpcTest {
    protected static Logger LOGGER = LoggerFactory.getLogger(TestRpcPress.class);

    @Autowired
    private RpcServiceProxy proxy;

    public static class HelloThread extends Thread {
        private CountDownLatch startLatch;
        private CountDownLatch allFinishLatch;
        private int threadId;
        private RpcServiceProxy proxy;

        public HelloThread(int threadId, CountDownLatch start, CountDownLatch finish, RpcServiceProxy proxy) {
            this.threadId = threadId;
            startLatch = start;
            allFinishLatch = finish;
            this.proxy = proxy;
        }

        public void run() {
            try {
                startLatch.await();
                IHelloService helloService = proxy.syncCreate(IHelloService.class);
                String result = helloService.hello("123456");
                LOGGER.info("testHello_threadid" + threadId, result);
                allFinishLatch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RpcException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testRpcPress() throws InterruptedException {
        int threadNum = 2000; //多线程并发
        long start = System.currentTimeMillis();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch allFinish = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            new HelloThread(i, startLatch, allFinish, proxy).start();
        }
        startLatch.countDown();
        allFinish.await();
        long end = System.currentTimeMillis();
        long total = end - start;
        long avg = total / threadNum;
        long runTimes = threadNum;

        printResult("TestRpc total run ", runTimes + " times");
        printResult("TestRpc total cost time", total + "ms");
        printResult("TestRpc avg cost time", avg + "ms");
    }
}

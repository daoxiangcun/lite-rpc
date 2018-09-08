package com.yhl.rpc.client;

import com.yhl.rpc.common.RpcException;
import com.yhl.rpc.inf.IHelloService;
import com.yhl.rpc.inf.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by daoxiangcun on 17-8-14.
 */
@RunWith(JUnit4Log4jClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml"})
public class RpcPressTest extends BaseRpcTest {
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
                for (int i = 0; i < 100; i++) {
                    String result = callRpcFunc(helloService, i);
                    LOGGER.info("===testHello_threadid" + threadId + ", i=" + i, result);
                }
                allFinishLatch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RpcException e) {
                e.printStackTrace();
            }
        }

        private String callRpcFunc(IHelloService helloService, int index) {
            int funcNo = new Random().nextInt(3);
            String result = null;
            if (funcNo == 0) {
                result = helloService.hello("123456");
            } else if (funcNo == 1) {
                result = String.valueOf(helloService.doubleInt(10));
            } else if (funcNo == 2) {
                User user = new User();
                user.setName("yhl");
                result = helloService.helloUser(user);
            }
            return result;
        }
    }

    /**
     * 5000个线程并发，每个线程随机调用100次接口
     *
     * @throws InterruptedException
     */
    @Test
    public void testRpcPress() throws InterruptedException {
        int threadNum = 5000; //5000个线程并发
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
        long runTimes = threadNum * 100;

        printResult("TestRpc total run ", runTimes + " times");     // 500000
        printResult("TestRpc total cost time", total + "ms");       // 20641ms
        printResult("TestRpc avg cost time", avg + "ms");           // 4ms, qps=24223
    }
}

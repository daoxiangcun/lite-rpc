package com.yhl.rpc.client;

import com.yhl.rpc.common.RpcFuture;
import com.yhl.rpc.common.RpcServiceResponse;
import com.yhl.rpc.common.inf.IHelloService;
import com.yhl.rpc.common.inf.IRpcResponseListener;
import com.yhl.rpc.common.inf.IUserInfoService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yuhongliang on 17-8-1.
 */
@RunWith(JUnit4Log4jClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml"})
public class TestRpcNormal extends BaseRpcTest {
    protected static Logger LOGGER = LoggerFactory.getLogger(TestRpcNormal.class);

    @Autowired
    private RpcServiceProxy proxy;

    @BeforeClass
    public static void init() {
        System.setProperty("zookeeper.host", "staging");
        ///BasicConfigurator.configure();
        LOGGER.info("init finish");
    }

    @Before
    public void setup() {
    }

    @After
    public void destroy() {
        LOGGER.info("finish destroy");
    }

    @Test
    public void testHelloOneTime() throws Exception {
        IHelloService helloService = proxy.syncCreate(IHelloService.class);
        Assert.assertNotNull(helloService);
        String result = helloService.hello("123456");
        printResult("testHello", result);

        int doubleValue = helloService.doubleInt(10);
        printResult("doubleValue of 10 ", doubleValue);
    }

    @Test
    public void testHelloMultiTimes() throws Exception {
        IHelloService helloService = proxy.syncCreate(IHelloService.class);
        Assert.assertNotNull(helloService);
        String result = helloService.hello("123456");
        printResult("testHello", result);

        int times = 100;
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            result = helloService.hello("00000" + i);
        }
        long end = System.currentTimeMillis();
        long total = end - start;
        long avg = total/times;
        printResult("totalTime", total + "ms");
        printResult("avgTime", avg + "ms");
    }

    @Test
    public void testUserInfo() throws Exception {
        IUserInfoService userInfoService = proxy.syncCreate(IUserInfoService.class);
        Assert.assertNotNull(userInfoService);
        Map<String, String> result222 = userInfoService.getProcessPayByProcessId("logId_" + UUID.randomUUID().toString(), 3816960L, "processKey_" + new Random().nextInt(10000), (byte)1);
        Assert.assertNotNull(result222);

        printResult("testUserInfo", result222);
    }

    @Test
    public void testUserInfo2() throws Exception {
        IUserInfoService userInfoService = proxy.syncCreate(IUserInfoService.class);
        Assert.assertNotNull(userInfoService);
        Map<String, String> result333 = userInfoService.getProcessPayByProcessId("logId_" + UUID.randomUUID().toString(), "processKey_" + new Random().nextInt(10000), (byte)1);
        Assert.assertNotNull(result333);

        printResult("testUserInfo2", result333);
    }

    @Test
    public void testAsyncUserInfoByFuture() throws Exception {
        RpcServiceProxy.AsyncProxyWrapper<IUserInfoService> userInfoServiceProxyWrapper = proxy.asyncCreate(IUserInfoService.class);
        Assert.assertNotNull(userInfoServiceProxyWrapper);

        Object fakeResult = userInfoServiceProxyWrapper.proxy.getProcessPayByProcessId("logId_" + UUID.randomUUID().toString(), "processKey_" + new Random().nextInt(10000), (byte)1);
        // do something

        // and then get result
        RpcServiceResponse realResponse = (RpcServiceResponse)userInfoServiceProxyWrapper.future.get();
        Map<String, String> realResult = (Map<String,String>)realResponse.getResponse();

        printResult("testAsyncUserInfo", realResult);
    }

    @Test
    public void testAsyncUserInfoByListener() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        RpcServiceProxy.AsyncProxyWrapper<IUserInfoService> userInfoServiceProxyWrapper = proxy.asyncCreateWithListener(IUserInfoService.class);
        Assert.assertNotNull(userInfoServiceProxyWrapper);

        Object fakeResult = userInfoServiceProxyWrapper.proxy.getProcessPayByProcessId("logId_" + UUID.randomUUID().toString(), "processKey_" + new Random().nextInt(10000), (byte) 1);
        userInfoServiceProxyWrapper.future.addListener(new IRpcResponseListener() {
            @Override
            public void onGetResponse(RpcServiceResponse response) {
                Map<String, String> realResult = (Map<String,String>)response.getResponse();
                printResult("11111111", realResult);
                latch.countDown();
            }
        });

        Object fakeResult2 = userInfoServiceProxyWrapper.proxy.getProcessPayByProcessId("logId_" + UUID.randomUUID().toString(), 10039471L, "processKey_" + new Random().nextInt(10000), (byte) 1);
        RpcFuture future = userInfoServiceProxyWrapper.future;
        future.addListener(new IRpcResponseListener() {
            @Override
            public void onGetResponse(RpcServiceResponse response) {
                Map<String, String> realResult = (Map<String,String>)response.getResponse();
                printResult("2222222222", realResult);
                latch.countDown();
            }
        });
        latch.await();
    }
}

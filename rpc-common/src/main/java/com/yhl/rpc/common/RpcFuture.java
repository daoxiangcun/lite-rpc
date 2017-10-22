package com.yhl.rpc.common;

import com.xiaomi.miliao.common.ConcurrentHashSet;
import com.yhl.rpc.common.inf.IRpcResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yuhongliang on 17-7-31.
 */
public class RpcFuture {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcFuture.class);
    private static final Map<String, NettyChannel> CHANNELS = new ConcurrentHashMap<String, NettyChannel>();
    private static final Map<String, RpcFuture> FUTURES = new ConcurrentHashMap<String, RpcFuture>();

    public static AtomicInteger is = new AtomicInteger();
    public static AtomicInteger ir = new AtomicInteger();

    private final String id;
    private final NettyChannel channel;
    private final RpcServiceRequest request;
    private final long createTime;
    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    private RpcServiceResponse response;

    private static ConcurrentHashSet<String> sFailedSessionId = new ConcurrentHashSet<String>();
    private IRpcResponseListener respListener;

    public RpcFuture(NettyChannel channel, RpcServiceRequest request) {
        this.channel = channel;
        this.request = request;
        this.id = request.getRequestId();
        this.createTime = System.currentTimeMillis();
        FUTURES.put(id, this);
        CHANNELS.put(id, channel);
        is.incrementAndGet();
    }

    public Object get(String sessionId) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            Object result = get(sessionId, Constants.DEFAULT_TIMEOUT);
            return result;
        } catch (Exception e) {
            LOGGER.error("RpcFuture.get exception, sessionId:{}, start get time is:{}(ms)", sessionId, startTime);
            sFailedSessionId.add(sessionId);
            throw e;
        }
    }

    public Object get() throws Exception {
        return get(Constants.DEFAULT_TIMEOUT);
    }

    public Object get(String sessionId, int timeout) throws Exception {
        try {
            Object retObj = get(timeout);
            return retObj;
        } catch (Exception e) {
            throw new Exception("timeout happen, sessionId is:" + sessionId);
        }
    }

    public Object get(int timeout) throws Exception {
        if (timeout <= 0) {
            timeout = Constants.DEFAULT_TIMEOUT;
        }
        if (!isDone()) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                while (!isDone()) {
                    done.await(timeout, TimeUnit.MILLISECONDS);
                    if (isDone() || System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
            if (!isDone()) {
                throw new Exception("timeout happen");
            }
        }
        return returnFromResponse();
    }

    public void addListener(IRpcResponseListener listener) {
        respListener = listener;
    }

    public Object returnFromResponse() {
        RpcServiceResponse msg = response;
        if (msg == null) {
            throw new IllegalStateException("response cannot be null");
        }
        return msg;
    }

    public boolean isDone() {
        return response != null;
    }

    public static void received(NettyChannel channel, RpcServiceResponse response) {
        String sessionId = response.getRequestId();
        try {
            RpcFuture future = FUTURES.remove(sessionId);
            ir.incrementAndGet();
            if (future != null) {
                if (sFailedSessionId.contains(sessionId)) {
                    LOGGER.error("exception sessionId {} receive server's response at {}(ms)", sessionId, System.currentTimeMillis());
                }
                future.doReceive(response);
            } else {
                LOGGER.info("The timeout response finally returned");
            }
        } finally {
            CHANNELS.remove(sessionId);
        }
    }

    private void doReceive(RpcServiceResponse res) {
        lock.lock();
        try {
            response = res;
            if (done != null) {
                done.signal();
            }
            LOGGER.info("=======doReceive, respListener = {}", respListener);
            if (respListener != null) {
                respListener.onGetResponse(response);
            }
        } finally {
            lock.unlock();
        }
    }

    public void cancel() {
        RpcServiceResponse errResult = new RpcServiceResponse();
        errResult.setRequestId(id);
        errResult.setErrCode(Constants.ERR_CODE_CANCEL);
        String errMessage = "request future has been canceled.";
        errResult.setResponse(errMessage);
        response = errResult;
        FUTURES.remove(id);
        CHANNELS.remove(id);
    }

    //检测和监控过期的数据
    static {
        Thread cleanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LOGGER.info("cleanThread run");
                    int ii = 0;
                    while (true) {
                        List<String> toDeleteList = new ArrayList<String>();
                        for (String item : FUTURES.keySet()) {
                            RpcFuture f = FUTURES.get(item);
                            // 超过10s未回应
                            if (f != null && f.createTime + 10000L < System.currentTimeMillis()) {
                                toDeleteList.add(item);
                            }
                        }
                        if (!toDeleteList.isEmpty()) {
                            for (String item : toDeleteList) {
                                FUTURES.remove(item);
                                CHANNELS.remove(item);
                            }
                        }
                        Thread.sleep(500);
                        if (ii++ > 20) {
                            ii = 0;
                            LOGGER.info("RpcFuture : is => {}, ir => {}, size => {}", new Object[]{is.get(), ir.get(), FUTURES.size()});
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Exception happen : {}", e.getMessage(), e);
                }
            }
        });
        cleanThread.setDaemon(true);
        cleanThread.start();
    }
}

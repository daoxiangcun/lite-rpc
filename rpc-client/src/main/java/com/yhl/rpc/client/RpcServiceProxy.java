package com.yhl.rpc.client;

import com.yhl.rpc.common.RpcException;
import com.yhl.rpc.common.RpcFuture;
import com.yhl.rpc.common.RpcServerInfo;
import com.yhl.rpc.common.RpcServiceRequest;
import com.yhl.rpc.common.RpcServiceResponse;
import com.yhl.rpc.common.inf.IRpcResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Created by yuhongliang on 17-7-31.
 */
@Service
public class RpcServiceProxy {
    private static Logger LOGGER = LoggerFactory.getLogger(RpcServiceProxy.class);
    private static final int DEFAULT_TIME_OUT = 5000; // 默认超时5s

    @Autowired
    RpcServiceChooser rpcServiceChooser;

    @Autowired
    RpcAgentPool rpcAgentPool;

    public<T> T syncCreate(Class<?> infClazz) throws RpcException {
        final RpcServerInfo serverInfo = rpcServiceChooser.chooseOneServer(infClazz.getName());
        if (serverInfo == null) {
            LOGGER.error("syncCreate, serverInfo is null");
            return null;
        }
        return (T)Proxy.newProxyInstance(infClazz.getClassLoader(), new Class<?>[]{infClazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcServiceRequest request = new RpcServiceRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setClassName(method.getDeclaringClass().getName());
                request.setMethodName(method.getName());
                request.setParamTypes(method.getParameterTypes());
                request.setParams(args);

                // 获取server信息,发送request并等待返回
                try {
                    long startTime = System.currentTimeMillis();
                    RpcServiceResponse response = rpcAgentPool.sendRequestAndWaitResponse(serverInfo, request, DEFAULT_TIME_OUT);
                    long endTime = System.currentTimeMillis();
                    long cost = endTime - startTime;
                    if (cost > 500) {
                        LOGGER.error("call {} cost {}ms, serverInfo:{}, request:{}", new Object[]{method.getName(), cost, serverInfo, request});
                    }
                    return response.getResponse();
                } catch (RpcException e) {
                    LOGGER.error("exception happen:{}", e.getMessage(), e);
                    return null;
                }
            }
        });
    }

    public<T> AsyncProxyWrapper<T> asyncCreate(Class<?> infClazz) {
        final AsyncProxyWrapper<T> proxyWrapper = new AsyncProxyWrapper<T>();

        final RpcServerInfo serverInfo = rpcServiceChooser.chooseOneServer(infClazz.getName());
        if (serverInfo == null) {
            LOGGER.error("asyncCreate, serverInfo is null");
            return null;
        }
        T proxy =  (T)Proxy.newProxyInstance(infClazz.getClassLoader(), new Class<?>[]{infClazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcServiceRequest request = new RpcServiceRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setClassName(method.getDeclaringClass().getName());
                request.setMethodName(method.getName());
                request.setParamTypes(method.getParameterTypes());
                request.setParams(args);

                RpcFuture future = rpcAgentPool.sendRequestAsync(serverInfo, request);
                proxyWrapper.future = future;
                Class<?> returnClass = method.getReturnType();
                Object retObj = returnClass.newInstance();
                return retObj;//fake object
            }
        });
        proxyWrapper.proxy = proxy;
        return proxyWrapper;
    }

    public<T> AsyncProxyWrapper<T> asyncCreateWithListener(Class<?> infClazz) {
        final AsyncProxyWrapper<T> proxyWrapper = new AsyncProxyWrapper<T>();

        final RpcServerInfo serverInfo = rpcServiceChooser.chooseOneServer(infClazz.getName());
        if (serverInfo == null) {
            LOGGER.error("asyncCreate, serverInfo is null");
            return null;
        }
        T proxy =  (T)Proxy.newProxyInstance(infClazz.getClassLoader(), new Class<?>[]{infClazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcServiceRequest request = new RpcServiceRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setClassName(method.getDeclaringClass().getName());
                request.setMethodName(method.getName());
                request.setParamTypes(method.getParameterTypes());
                request.setParams(args);

                int argLen = args.length;
                Object lastArg = args[argLen-1];
                if (lastArg instanceof IRpcResponseListener) {
                    proxyWrapper.listener = (IRpcResponseListener)lastArg;
                }
                RpcFuture future = rpcAgentPool.sendRequestAsync(serverInfo, request);
//                future.addListener(proxyWrapper.listener);
                proxyWrapper.future = future;
                Class<?> returnClass = method.getReturnType();
                Object retObj = returnClass.newInstance();
                return retObj;//fake object
            }
        });
        proxyWrapper.proxy = proxy;
        return proxyWrapper;
    }

    public static class AsyncProxyWrapper<T> {
        public T proxy;
        public RpcFuture future;
        public IRpcResponseListener listener;
    }
}

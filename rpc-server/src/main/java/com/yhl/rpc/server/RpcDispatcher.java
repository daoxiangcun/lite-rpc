package com.yhl.rpc.server;

import com.yhl.rpc.common.Constants;
import com.yhl.rpc.common.RpcServiceRequest;
import com.yhl.rpc.common.RpcServiceResponse;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcDispatcher {
    private static Logger LOGGER = LoggerFactory.getLogger(RpcDispatcher.class);
    private Map<String, Object> classNameBeanMap;
    private ThreadPoolExecutor executor = null;

    public RpcDispatcher(Map<String, Object> classNameBeanMap) {
        this.classNameBeanMap = classNameBeanMap;
        executor = new ThreadPoolExecutor(20, 20, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
    }

    public void dispatch(ChannelHandlerContext ctx, RpcServiceRequest request) {
        LOGGER.info("RpcDispatcher, request:{}", request);
        submitRpcRequest(ctx, request);
    }

    private void submitRpcRequest(final ChannelHandlerContext ctx, final RpcServiceRequest request) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String className = request.getClassName();
                String methodName = request.getMethodName();
                Class<?>[] paramTypes = request.getParamTypes();
                Object[] params = request.getParams();
                Object result = null;
                try {
                    Class<?> serviceClass = Class.forName(className);
                    Method method = serviceClass.getMethod(methodName, paramTypes);
                    result = method.invoke(classNameBeanMap.get(className), params);
                } catch (Exception e) {
                    LOGGER.error("exception happen when dispatch:{}", e.getMessage(), e);
                }
                RpcServiceResponse response = new RpcServiceResponse();
                response.setRequestId(request.getRequestId());
                response.setErrCode(result != null ? Constants.ERR_CODE_OK : Constants.ERR_CODE_CANCEL);
                LOGGER.info("RpcDispatcher, result={}", result);
                if (result != null) {
                    response.setResponse(result);
                }
                ctx.writeAndFlush(response);
            }
        });
    }
}

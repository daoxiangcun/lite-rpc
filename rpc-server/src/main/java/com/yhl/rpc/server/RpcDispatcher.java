package com.yhl.rpc.server;

import com.yhl.rpc.common.Constants;
import com.yhl.rpc.common.model.RpcServiceRequest;
import com.yhl.rpc.common.model.RpcServiceResponse;
import com.yhl.rpc.server.servicehandler.ServiceImplHandlerManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcDispatcher {
    private static Logger LOGGER = LoggerFactory.getLogger(RpcDispatcher.class);
    private ServiceImplHandlerManager serviceImplHandlerManager;
    private ThreadPoolExecutor executor = null;

    public RpcDispatcher(ServiceImplHandlerManager serviceImplHandlerManager) {
        this.serviceImplHandlerManager = serviceImplHandlerManager;
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
                    result = serviceImplHandlerManager.handle(className, methodName, paramTypes, params);
                } catch (Exception e) {
                    LOGGER.error("exception happen when dispatch:{}", e.getMessage(), e);
                }
                RpcServiceResponse response = new RpcServiceResponse();
                response.setRequestId(request.getRequestId());
                response.setErrCode(result != null ? Constants.ERR_CODE_OK : Constants.ERR_CODE_ERROR);
                LOGGER.info("RpcDispatcher, result = {}", result);
                if (result != null) {
                    response.setResponse(result);
                }
                ctx.writeAndFlush(response);
            }
        });
    }
}

package com.yhl.rpc.client;

import com.yhl.rpc.common.NettyChannel;
import com.yhl.rpc.common.RpcFuture;
import com.yhl.rpc.common.RpcServiceResponse;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcNettyClientHandler extends ChannelHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcNettyClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcServiceResponse response = (RpcServiceResponse)msg;
        LOGGER.info("channelRead, response:{}, result:{}", response, response.getResponse());
        RpcFuture.received(NettyChannel.getOrAddChannel(ctx.channel()), response);
    }
}

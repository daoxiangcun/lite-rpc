package com.yhl.rpc.server;

import com.yhl.rpc.common.NetUtils;
import com.yhl.rpc.common.NettyChannel;
import com.yhl.rpc.common.RpcServiceRequest;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcNettyServerHandler extends ChannelHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcNettyServerHandler.class);
    private final Map<String, NettyChannel> channels = new ConcurrentHashMap<String, NettyChannel>();
    RpcDispatcher dispatcher = null;

    public RpcNettyServerHandler(RpcDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel());
        try {
            if (channel != null) {
                channels.put(NetUtils.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()), channel);
            }
            ctx.fireChannelActive();
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            channels.remove(NetUtils.toAddressString((InetSocketAddress)ctx.channel().remoteAddress()));
            ctx.fireChannelInactive();
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOGGER.info("channelRead, msg:{}", msg);
        try {
            dispatcher.dispatch(ctx, (RpcServiceRequest) msg);
        } catch (Throwable t) {
            LOGGER.error("Process failed [channel info {}, message info : {}], exception:{}", new Object[]{NettyChannel.getOrAddChannel(ctx.channel()), msg, t.getMessage(), t});
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}

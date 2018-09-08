package com.yhl.rpc.client;

import com.yhl.rpc.common.NettyChannel;
import com.yhl.rpc.common.RpcFuture;
import com.yhl.rpc.common.codec.RpcMessageDecoder;
import com.yhl.rpc.common.codec.RpcMessageEncoder;
import com.yhl.rpc.common.model.RpcServiceRequest;
import com.yhl.rpc.common.model.RpcServiceResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.yhl.rpc.common.Constants;

public class RpcNettyClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcNettyClient.class);
    private RpcAgent agent;
    EventLoopGroup group = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();
    Channel channel;

    public RpcNettyClient(RpcAgent agent) {
        this.agent = agent;
    }

    protected void doOpen() {
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new RpcMessageEncoder());
                        ch.pipeline().addLast(new RpcMessageDecoder(1024, 0, 4, RpcServiceResponse.class));
                        ch.pipeline().addLast(new RpcNettyClientHandler());
                    }
                });
    }

    public void doConnect() throws Throwable {
        long start = System.currentTimeMillis();
        ChannelFuture future = bootstrap.connect(getConnectAddress());
        try {
            boolean ret = future.awaitUninterruptibly(1000, TimeUnit.MILLISECONDS);
            if (ret && future.isSuccess()) {
                Channel newChannel = future.channel();
                try {
                    Channel oldChannel = RpcNettyClient.this.channel;
                    if (oldChannel != null) {
                        try {
                            oldChannel.close();
                        } finally {
                            NettyChannel.removeChannelIfDisconnected(oldChannel);
                        }
                    }
                } finally {
                    RpcNettyClient.this.channel = newChannel;
                }
            } else if (future.cause() != null) {
                throw future.cause();
            } else {
                throw new TimeoutException("connect " + getConnectAddress() + "failed!");
            }
        } finally {
            if (channel != null && !channel.isActive()) {
                future.channel().close();
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.info("connect {} , cost time {}ms", getConnectAddress(), (end - start));
    }

    public boolean isConnected() {
        if (channel == null) {
            return false;
        }
        return channel.isActive();
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
    }

    public RpcFuture rpcRequest(RpcServiceRequest serviceRequest) throws Exception {
        RpcFuture future = new RpcFuture(NettyChannel.getOrAddChannel(channel), serviceRequest);
        try {
            channel.writeAndFlush(serviceRequest);
        } catch (Exception e) {
            LOGGER.error("RpcNettyClient rpcRequest serviceRequest : {}, exception : {}", new Object[]{serviceRequest, e.getMessage(), e});
            future.cancel();
            throw e;
        }
        return future;
    }

    private InetSocketAddress getConnectAddress() {
        return new InetSocketAddress(Constants.LOCAL_IP, Constants.RPC_SERVER_PORT);
    }
}

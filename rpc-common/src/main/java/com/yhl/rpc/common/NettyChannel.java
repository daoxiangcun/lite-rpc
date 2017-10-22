package com.yhl.rpc.common;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NettyChannel {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyChannel.class);

    private static final ConcurrentMap<Channel, NettyChannel> channelMap = new ConcurrentHashMap<Channel, NettyChannel>();
    private final Channel channel;

    private NettyChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("netty channel == null");
        }
        this.channel = channel;
    }

    public static NettyChannel getOrAddChannel(Channel ch) {
        if (ch == null) {
            return null;
        }
        NettyChannel ret = channelMap.get(ch);
        if (ret == null) {
            NettyChannel nc = new NettyChannel(ch);
            if (ch.isActive()) {
                ret = channelMap.putIfAbsent(ch, nc);
            }
            if (ret == null) {
                ret = nc;
            }
        }
        return ret;
    }

    public static void removeChannelIfDisconnected(Channel ch) {
        if (ch != null && !ch.isActive()) {
            channelMap.remove(ch);
        }
    }

    public static Collection<NettyChannel> getNettyChannels() {
        return channelMap.values();
    }

    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    public boolean isActive() {
        return channel.isActive();
    }

    public Channel getChannel() {
        return channel;
    }

    public void send(Object message, boolean sent) throws Exception {
        boolean success = true;
        int timeout = 0;
        try {
            ChannelFuture future = channel.writeAndFlush(message);
            if (sent) {
                timeout = 1000;
                success = future.await(timeout);
            }
            Throwable cause = future.cause();
            if (cause != null) {
                throw cause;
            }
        }catch (Throwable e) {
            LOGGER.error("send fail, message is:[{}], sent is:{}, exception is:{}", new Object[]{message, sent, e.getMessage()}, e);
            throw new Exception(e);
            ////throw new Exception("Failed to send message " + message + " to " + getRemoteAddress() + ", cause: " + e.getMessage());
        }
        if (!success) {
            throw new Exception("Failed to send message " + message + " to " + getRemoteAddress() + " in tomeout(" + timeout + "ms) limit");
        }
    }

    public void close() {
        try {
            removeChannelIfDisconnected(channel);
            if (channel != null) {
                channel.close();
            }
        } catch (Exception e) {
            LOGGER.error("close() error, message:{}", e.getMessage(), e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (channel == null ? 0 : channel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NettyChannel other = (NettyChannel) obj;
        if (channel == null) {
            if (other.channel != null) {
                return false;
            }
        } else if (!channel.equals(other.channel)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NettyChannel [channel=" + channel + "]";
    }
}

package com.yhl.rpc.server;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.yhl.rpc.common.*;
import com.yhl.rpc.common.model.RpcServiceRequest;
import com.yhl.rpc.common.utils.NetUtils;
import com.yhl.rpc.server.servicehandler.ServiceImplHandlerManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Map;

public class RpcNettyServer implements ApplicationContextAware, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcNettyServer.class);

    private int bossThreadNum = 5;
    private int workerThreadNum = 20;

    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;
    private ApplicationContext applicationContext;

    private boolean inited;
    private int port;
    @Autowired
    private ServiceImplHandlerManager serviceHandlerManager;
    private RpcDispatcher dispatcher;
    private RpcServiceRegister rpcServiceRegister;
    private Map<String, Object> serviceMap = Maps.newHashMap();

    public RpcNettyServer(int port) {
        this.port = port;
        this.rpcServiceRegister = new RpcServiceRegister();
        this.inited = true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static String getServiceDefinitionClass(Class<?> serviceImplClass) {
        Class<?>[] interfaces = serviceImplClass.getInterfaces();
        for (Class<?> i : interfaces) {
            String interfaceName = i.getName();
            LOGGER.debug("interfaceName is {}", interfaceName);
            return interfaceName;  // 这里只有RPCService一个接口，所以直接返回
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final Map<String, Object> rpcServiceMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        for (final Object one : rpcServiceMap.values()) {
            final Class<? extends Object> permissionClass = one.getClass();
            String interfaceName = getServiceDefinitionClass(permissionClass);
            if (!Strings.isNullOrEmpty(interfaceName)) {
                serviceMap.put(interfaceName, one);
            }
        }
        LOGGER.info("afterPropertiesSet, serviceMap is:{}", serviceMap);
        serviceHandlerManager.init(serviceMap);
        dispatcher = new RpcDispatcher(serviceHandlerManager);
        start();
    }

    private void bind() {
        bossGroup = new NioEventLoopGroup(bossThreadNum);
        workerGroup = new NioEventLoopGroup(workerThreadNum);
        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new RpcMessageEncoder());
                            ch.pipeline().addLast(new RpcMessageDecoder(1024, 0, 4, RpcServiceRequest.class));
                            ch.pipeline().addLast(new RpcNettyServerHandler(dispatcher));
                        }
                    });

            ChannelFuture f = bootstrap.bind(port).sync();
            LOGGER.warn("RpcNettyServer bind port {} success", port);
            channel = f.channel();
            // 注册service到zookeeper
            for (Map.Entry<String, Object> service : serviceMap.entrySet()) {
                rpcServiceRegister.registerToZk(NetUtils.getLocalHost(), String.valueOf(port), service.getKey());
            }
        } catch (Exception e) {
            LOGGER.error("Exception when init rpc server, {}", e.getMessage(), e);
        } finally {
        }
    }

    public void start() {
        bind();
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    static class ShutdownHook extends Thread {
        RpcNettyServer server;

        public ShutdownHook(RpcNettyServer server) {
            this.server = server;
        }

        @Override
        public void run() {
            LOGGER.info("NettyServer is closing");
            Collection<NettyChannel> channels = NettyChannel.getNettyChannels();
            for (NettyChannel channel : channels) {
                try {
                    channel.getChannel().close().sync();
                } catch (InterruptedException e) {
                }
                channel.close();
            }
            server.stop();
            LOGGER.warn("NettyServer shutdown finish");
        }
    }

    public boolean isInited() {
        return inited;
    }

    public void setInited(boolean inited) {
        this.inited = inited;
    }
}

package com.zxs.vici.rpc.common.transport.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zxs.vici.rpc.common.config.RpcConfig;
import com.zxs.vici.rpc.common.spring.RpcBeanPostProcessor;
import com.zxs.vici.rpc.common.transport.codec.RpcMessageDecoder;
import com.zxs.vici.rpc.common.transport.codec.RpcMessageEncode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Component
public class RpcServer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    @Resource
    private RpcMessageEncode encode;
    @Resource
    private RpcMessageDecoder decoder;
    @Resource
    private RpcServerHandler handler;
    @Resource
    private RpcConfig rpcConfig;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private DefaultEventExecutorGroup executors;

    @SneakyThrows
    public void start() {
        RpcConfig.Server serverConfig = rpcConfig.getServer();
        String host = InetAddress.getLocalHost().getHostAddress();
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("rpc-thread-%d").setDaemon(false).build();
        executors = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2, threadFactory);
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new IdleStateHandler(serverConfig.getReaderIdleTime(), 0, 0, TimeUnit.MILLISECONDS));
                            pipeline.addLast(encode);
                            pipeline.addLast(decoder);
                            pipeline.addLast(executors, handler);
                        }

                    });
            server.bind(host, serverConfig.getPort()).sync();
            LOGGER.info("rpc服务端启动成功");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("RPC服务端启动失败");
        }
    }

    @Override
    public void run(String... args) throws Exception {
        if (rpcConfig.getServer().getEnable() && RpcBeanPostProcessor.SERVICE_COUNT.get() > 0) {
            this.start();
        } else {
            LOGGER.info("本程序无RPC服务, 未启动RPC服务端");
        }
    }

    @PreDestroy
    public void destroy() throws Exception {
        if (rpcConfig.getServer().getEnable() && RpcBeanPostProcessor.SERVICE_COUNT.get() > 0) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            executors.shutdownGracefully();
            LOGGER.info("RPC服务端关闭");
        }
    }
}

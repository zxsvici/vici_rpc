package com.zxs.vici.rpc.common.transport.client;

import com.zxs.vici.rpc.common.config.RpcConfig;
import com.zxs.vici.rpc.common.constraint.RpcConstants;
import com.zxs.vici.rpc.common.model.dto.RpcMessage;
import com.zxs.vici.rpc.common.model.dto.RpcRequest;
import com.zxs.vici.rpc.common.model.dto.RpcResponse;
import com.zxs.vici.rpc.common.register.RpcServiceDiscovery;
import com.zxs.vici.rpc.common.spring.RpcBeanPostProcessor;
import com.zxs.vici.rpc.common.transport.RpcRequestTransport;
import com.zxs.vici.rpc.common.transport.codec.RpcMessageDecoder;
import com.zxs.vici.rpc.common.transport.codec.RpcMessageEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * initialize and close Bootstrap object
 *
 * @author shuang.kou
 * @createTime 2020年05月29日 17:51:00
 */
@Component
public final class RpcClient implements RpcRequestTransport, CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    @Resource
    private RpcServiceDiscovery discovery;
    @Resource
    private UnprocessedRequests requests;
    @Resource
    private ChannelProvider channelProvider;
    @Resource
    private RpcClientHandler handler;
    @Resource
    private RpcMessageEncode encode;
    @Resource
    private RpcMessageDecoder decoder;
    @Resource
    private RpcConfig rpcConfig;

    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;

    private void init() {
        RpcConfig.Client client = rpcConfig.getClient();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, client.getConnectTimeout())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, client.getWriteIdleTime(), 0, TimeUnit.MILLISECONDS));
                        pipeline.addLast(encode);
                        pipeline.addLast(decoder);
                        pipeline.addLast(handler);
                    }
                });
    }

    /**
     * connect server and get the channel ,so that you can send rpc message to server
     *
     * @param inetSocketAddress server address
     * @return the channel
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                LOGGER.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {

        RpcConfig.Client client = rpcConfig.getClient();

        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();

        InetSocketAddress inetSocketAddress = discovery.lookupService(rpcRequest);

        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            requests.put(rpcRequest.getRequestId(), resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder()
                    .data(rpcRequest)
                    .codec(client.getSerializationType().getCode())
                    .compress(client.getCompressType().getCode())
                    .type(RpcConstants.REQUEST_TYPE).build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    LOGGER.info("client send message: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    LOGGER.error("Send failed:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }

        return resultFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    @PreDestroy
    public void close() {
        Optional.ofNullable(eventLoopGroup).ifPresent(event -> {
            event.shutdownGracefully();
            LOGGER.info("RPC客户端关闭成功");
        });
    }

    @Override
    public void run(String... args) throws Exception {
        if(rpcConfig.getClient().getEnable() && RpcBeanPostProcessor.AUTOWIRED_COUNT.get() > 0) {
            this.init();
        }else {
            LOGGER.info("本程序无RPC调用或RPC客户端未启用, 未启动RPC客户端");
        }
    }
}

package com.zxs.vici.rpc.common.proxy;

import com.zxs.vici.rpc.common.config.RpcServiceConfig;
import com.zxs.vici.rpc.common.enums.RpcMessageEnum;
import com.zxs.vici.rpc.common.exception.RpcException;
import com.zxs.vici.rpc.common.model.dto.RpcRequest;
import com.zxs.vici.rpc.common.model.dto.RpcResponse;
import com.zxs.vici.rpc.common.transport.RpcRequestTransport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class RpcClientProxy implements InvocationHandler {

    private final RpcRequestTransport transport;
    private final RpcServiceConfig config;

    public RpcClientProxy(RpcRequestTransport transport, RpcServiceConfig config) {
        this.transport = transport;
        this.config = config;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] {clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args)
                .requestId(UUID.randomUUID().toString())
                .name(config.getName())
                .version(config.getVersion())
                .build();
        RpcResponse<Object> response = null;
        CompletableFuture<RpcResponse<Object>> future = (CompletableFuture<RpcResponse<Object>>) transport.sendRpcRequest(request);
        response = future.get();
        this.check(request, response);
        return response.getData();
    }

    private void check(RpcRequest request, RpcResponse<Object> response) {

        Optional.ofNullable(response).orElseThrow(() -> RpcException.exceptionOf(RpcMessageEnum.FAIL));

        if(!Objects.equals(request.getRequestId(), response.getRequestId())) {
            throw RpcException.exceptionOf(RpcMessageEnum.RESPONSE_NOT_MATCH_REQUEST);
        }

        if(!Objects.equals(RpcMessageEnum.SUCCESS.getCode(), response.getCode())) {
            throw RpcException.exceptionOf(RpcMessageEnum.FAIL);
        }

    }
}

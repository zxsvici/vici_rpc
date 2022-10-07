package com.zxs.vici.rpc.common.handler;

import com.zxs.vici.rpc.common.exception.RpcException;
import com.zxs.vici.rpc.common.model.dto.RpcRequest;
import com.zxs.vici.rpc.common.provider.RpcServiceProvider;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class RpcRequestHandler {

    private final RpcServiceProvider provider;

    public RpcRequestHandler(RpcServiceProvider provider) {
        this.provider = provider;
    }

    public Object handle(RpcRequest request) {
        Object service = provider.getService(request.getRpcServiceName());
        Object result;
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            result = method.invoke(service, request.getParameters());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}

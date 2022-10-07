package com.zxs.vici.rpc.common.provider.impl;

import com.zxs.vici.rpc.common.config.RpcServiceConfig;
import com.zxs.vici.rpc.common.enums.RpcMessageEnum;
import com.zxs.vici.rpc.common.exception.RpcException;
import com.zxs.vici.rpc.common.provider.RpcServiceProvider;
import com.zxs.vici.rpc.common.register.redis.RedisRpcServiceRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RedisRpcServiceProviderImpl implements RpcServiceProvider {

    private static final Map<String, Object> registryMap = new ConcurrentHashMap<>();

    @Value("${rpc.server.port}")
    private Integer port;
    @Resource
    private RedisRpcServiceRegistry registry;

    @Override
    public void addService(RpcServiceConfig config) {
        String rpcName = config.getRpcName();
        if(registryMap.containsKey(rpcName)) {
            return;
        }
        registryMap.put(rpcName, config.getService());
        try {
            registry.registerService(rpcName, new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), port));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getService(String name) {
        Object result = registryMap.get(name);
        Optional.ofNullable(result).orElseThrow(() -> RpcException.exceptionOf(RpcMessageEnum.SERVICE_CAN_NOT_BE_FOUND));
        return result;
    }
}

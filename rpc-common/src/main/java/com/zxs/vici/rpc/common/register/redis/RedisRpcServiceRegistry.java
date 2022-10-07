package com.zxs.vici.rpc.common.register.redis;

import com.zxs.vici.rpc.common.register.RpcServiceRegistry;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Component
public class RedisRpcServiceRegistry implements RpcServiceRegistry {

    @Resource
    private StringRedisTemplate template;

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        template.opsForValue().set(rpcServiceName, inetSocketAddress.getAddress().getHostAddress() + "-" + inetSocketAddress.getPort());
    }
}

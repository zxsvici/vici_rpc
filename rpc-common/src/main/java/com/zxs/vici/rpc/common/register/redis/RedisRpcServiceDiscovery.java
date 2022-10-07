package com.zxs.vici.rpc.common.register.redis;

import com.zxs.vici.rpc.common.model.dto.RpcRequest;
import com.zxs.vici.rpc.common.register.RpcServiceDiscovery;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Component
public class RedisRpcServiceDiscovery implements RpcServiceDiscovery {

    @Resource
    private StringRedisTemplate template;

    @Override
    public InetSocketAddress lookupService(RpcRequest request) {
        String value = template.opsForValue().get(request.getRpcServiceName());
        assert value != null;
        int index = value.lastIndexOf("-");
        return new InetSocketAddress(value.substring(0, index), Integer.parseInt(value.substring(index + 1)));
    }
}

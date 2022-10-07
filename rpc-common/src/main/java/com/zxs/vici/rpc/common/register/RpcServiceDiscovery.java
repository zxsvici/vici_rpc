package com.zxs.vici.rpc.common.register;

import com.zxs.vici.rpc.common.model.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * service discovery
 *
 * @author shuang.kou
 * @createTime 2020年06月01日 15:16:00
 */
public interface RpcServiceDiscovery {
    /**
     * lookup service by rpcServiceName
     *
     * @param request rpc service pojo
     * @return service address
     */
    InetSocketAddress lookupService(RpcRequest request);
}

package com.zxs.vici.rpc.common.provider;

import com.zxs.vici.rpc.common.config.RpcServiceConfig;

public interface RpcServiceProvider {

    void addService(RpcServiceConfig config);

    Object getService(String name);
}

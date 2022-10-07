package com.zxs.vici.rpc.common.transport;

import com.zxs.vici.rpc.common.model.dto.RpcRequest;

public interface RpcRequestTransport {

    Object sendRpcRequest(RpcRequest request);
}

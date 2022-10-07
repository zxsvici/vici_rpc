package com.zxs.vici.rpc.common.model.dto;

import com.zxs.vici.rpc.common.enums.RpcMessageEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = 5045302682000551513L;

    private String requestId;

    private Integer code;

    private T data;

    private String msg;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setCode(RpcMessageEnum.SUCCESS.getCode());
        response.setMsg(RpcMessageEnum.SUCCESS.getMsg());
        Optional.ofNullable(data).ifPresent(response::setData);
        return response;
    }

    public static <T> RpcResponse<T> fail(RpcMessageEnum responseEnum) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(responseEnum.getCode());
        response.setMsg(responseEnum.getMsg());
        return response;
    }
}

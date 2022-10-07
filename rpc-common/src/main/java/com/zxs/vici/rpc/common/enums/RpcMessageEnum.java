package com.zxs.vici.rpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum RpcMessageEnum {

    SUCCESS(200, "服务调用成功"),
    FAIL(500, "服务调用失败"),
    RESPONSE_NOT_MATCH_REQUEST(10001, "响应与请求不匹配"),
    SERVICE_CAN_NOT_BE_FOUND(10002, "没有找到指定的服务"),
    SERIALIZE_TYPE_NOT_EXISTS(10003, "序列化类型不存在"),
    COMPRESS_TYPE_NOT_EXISTS(10003, "压缩类型不存在");

    private final Integer code;

    private final String msg;
}

package com.zxs.vici.rpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum RpcRegisterTypeEnum {

    REDIS("redis");

    private final String name;
}

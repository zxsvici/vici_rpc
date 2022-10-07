package com.zxs.vici.rpc.common.enums;

import com.zxs.vici.rpc.common.exception.RpcException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CompressTypeEnum {
    GZIP((byte) 0x01, "gzip","gzipCompress");

    private final byte code;
    private final String name;
    private final String beanName;

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

    public static CompressTypeEnum nameOf(String name) {
        for (CompressTypeEnum value : values()) {
            if(value.name.equals(name)) {
                return value;
            }
        }
        throw RpcException.exceptionOf(RpcMessageEnum.COMPRESS_TYPE_NOT_EXISTS);
    }

    public static String getBeanName(byte code) {
        for (CompressTypeEnum value : values()) {
            if(value.code == code) {
                return value.getBeanName();
            }
        }
        throw RpcException.exceptionOf(RpcMessageEnum.SERIALIZE_TYPE_NOT_EXISTS);
    }
}

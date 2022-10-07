package com.zxs.vici.rpc.common.enums;

import com.zxs.vici.rpc.common.exception.RpcException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wangtao .
 * @createTime on 2020/10/2
 */
@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {

    KYRO((byte) 0x01, "kyro", "kryoSerializer"),
    PROTOSTUFF((byte) 0x02, "protostuff","protostuffSerializer"),
    HESSIAN((byte) 0X03, "hessian","hessianSerializer");

    private final byte code;
    private final String name;
    private final String beanName;

    public static String getName(byte code) {
        for (SerializationTypeEnum c : SerializationTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

    public static SerializationTypeEnum nameOf(String name) {
        for (SerializationTypeEnum value : values()) {
            if(value.name.equals(name)) {
                return value;
            }
        }
        throw RpcException.exceptionOf(RpcMessageEnum.SERIALIZE_TYPE_NOT_EXISTS);
    }

    public static String getBeanName(byte code) {
        for (SerializationTypeEnum value : values()) {
            if(value.code == code) {
                return value.getBeanName();
            }
        }
        throw RpcException.exceptionOf(RpcMessageEnum.SERIALIZE_TYPE_NOT_EXISTS);
    }
}

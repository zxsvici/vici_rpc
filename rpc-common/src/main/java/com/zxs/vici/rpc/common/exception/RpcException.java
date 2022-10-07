package com.zxs.vici.rpc.common.exception;

import com.zxs.vici.rpc.common.enums.RpcMessageEnum;

public class RpcException extends RuntimeException{

    private int code;

    public RpcException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public RpcException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public static RpcException exceptionOf(RpcMessageEnum messageEnum) {
        return new RpcException(messageEnum.getMsg(), messageEnum.getCode());
    }

    public static RpcException exceptionOf(RpcMessageEnum messageEnum, String msg) {
        return new RpcException(msg, messageEnum.getCode());
    }

    public static RpcException exceptionOf(int code, String msg) {
        return new RpcException(msg, code);
    }

    public int getCode() {
        return code;
    }
}

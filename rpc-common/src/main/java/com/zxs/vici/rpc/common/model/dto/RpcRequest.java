package com.zxs.vici.rpc.common.model.dto;

import com.zxs.vici.rpc.common.constraint.CommonConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1887708079076419890L;

    private String requestId;

    private String name;

    private String methodName;

    private Object[] parameters;

    private Class<?>[] paramTypes;

    private String version;

    public String getRpcServiceName() {
        return this.getName() + CommonConstraint.HORIZONTAL_LINE + this.getVersion();
    }
}

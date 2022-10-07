package com.zxs.vici.rpc.common.config;

import com.zxs.vici.rpc.common.constraint.CommonConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcServiceConfig {

    private String name;

    private String version;

    private Object service;

    public String getRpcName() {
        return this.name + CommonConstraint.HORIZONTAL_LINE + version;
    }
}

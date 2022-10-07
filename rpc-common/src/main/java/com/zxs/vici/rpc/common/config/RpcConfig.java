package com.zxs.vici.rpc.common.config;

import com.zxs.vici.rpc.common.enums.CompressTypeEnum;
import com.zxs.vici.rpc.common.enums.RpcRegisterTypeEnum;
import com.zxs.vici.rpc.common.enums.SerializationTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "rpc")
@Component
@Data
public class RpcConfig {

    private final RpcConfig.Server server = new Server();

    private final RpcConfig.Client client = new Client();

    @Data
    public static class Server {

        private Integer port = 8081;

        private Integer readerIdleTime = 30000;

        private RpcRegisterTypeEnum registerType = RpcRegisterTypeEnum.REDIS;

        private Boolean enable = true;
    }

    @Data
    public static class Client {

        private Integer connectTimeout = 30000;

        private Integer writeIdleTime = 30000;

        private CompressTypeEnum compressType = CompressTypeEnum.GZIP;

        private SerializationTypeEnum serializationType = SerializationTypeEnum.KYRO;

        private Boolean enable = true;
    }
}

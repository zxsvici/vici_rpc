package com.zxs.vici.rpc.common.transport.codec;

import com.zxs.vici.rpc.common.compress.Compress;
import com.zxs.vici.rpc.common.config.RpcConfig;
import com.zxs.vici.rpc.common.constraint.RpcConstants;
import com.zxs.vici.rpc.common.enums.CompressTypeEnum;
import com.zxs.vici.rpc.common.enums.SerializationTypeEnum;
import com.zxs.vici.rpc.common.model.dto.RpcMessage;
import com.zxs.vici.rpc.common.serialize.Serializer;
import com.zxs.vici.rpc.common.util.SpringContextUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RpcMessageEncode extends MessageToByteEncoder<RpcMessage> {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage message, ByteBuf out) throws Exception {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writeByte(RpcConstants.VERSION);
            // leave a place to write the value of full length
            out.writerIndex(out.writerIndex() + 4);
            byte messageType = message.getType();
            out.writeByte(messageType);
            out.writeByte(message.getCodec());
            out.writeByte(message.getCompress());
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());
            // build full length
            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            // if messageType is not heartbeat message,fullLength = head length + body length
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                    && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                // serialize the object
                String serializationBeanName = SerializationTypeEnum.getBeanName(message.getCodec());
                Serializer serializer = (Serializer) SpringContextUtils.getBean(serializationBeanName);
                bodyBytes = serializer.serialize(message.getData());
                // compress the bytes
                String compressBeanName = CompressTypeEnum.getBeanName(message.getCompress());
                Compress compress = (Compress) SpringContextUtils.getBean(compressBeanName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                out.writeBytes(bodyBytes);
            }
            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

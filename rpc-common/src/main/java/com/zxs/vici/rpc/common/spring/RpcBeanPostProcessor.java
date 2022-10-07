package com.zxs.vici.rpc.common.spring;

import com.zxs.vici.rpc.common.anno.RpcAutowired;
import com.zxs.vici.rpc.common.anno.RpcService;
import com.zxs.vici.rpc.common.config.RpcServiceConfig;
import com.zxs.vici.rpc.common.provider.RpcServiceProvider;
import com.zxs.vici.rpc.common.proxy.RpcClientProxy;
import com.zxs.vici.rpc.common.transport.RpcRequestTransport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RpcBeanPostProcessor implements BeanPostProcessor {

    public static final AtomicInteger SERVICE_COUNT = new AtomicInteger(0);
    public static final AtomicInteger AUTOWIRED_COUNT = new AtomicInteger(0);

    @Resource
    private RpcServiceProvider provider;
    @Resource
    private RpcRequestTransport transport;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Optional.ofNullable(bean.getClass().getAnnotation(RpcService.class)).ifPresent(annotation -> {
            RpcServiceConfig config = RpcServiceConfig.builder()
                    .service(bean)
                    .name(annotation.name())
                    .version(annotation.version())
                    .build();
            provider.addService(config);
            SERVICE_COUNT.incrementAndGet();
        });
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        for (Field field : bean.getClass().getDeclaredFields()) {

            Optional.ofNullable(field.getAnnotation(RpcAutowired.class)).ifPresent(annotation -> {
                RpcServiceConfig config = RpcServiceConfig.builder()
                        .name(annotation.name())
                        .version(annotation.version())
                        .build();
                RpcClientProxy clientProxy = new RpcClientProxy(transport, config);
                Object proxy = clientProxy.getProxy(field.getType());
                field.setAccessible(true);
                try {
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                AUTOWIRED_COUNT.incrementAndGet();
            });
        }
        return bean;
    }
}

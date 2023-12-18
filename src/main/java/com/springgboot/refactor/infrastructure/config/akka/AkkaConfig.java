package com.springgboot.refactor.infrastructure.config.akka;

import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import static com.springgboot.refactor.infrastructure.config.akka.AkkaConstant.AKKA_PRE_ADDRESS;

/**
 * Akka配置类
 *
 * @author Elliot
 */
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = AKKA_PRE_ADDRESS, name = "enable", havingValue = "true")
@Configuration
@EnableConfigurationProperties(
        {
                AkkaProperties.class
        }
)
public class AkkaConfig implements DisposableBean {

    private final ConfigurableApplicationContext configurableApplicationContext;

    @Bean
    public void initAkka() {
        // 获取配置文件 取当前环境
        ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();
        Binder binder = Binder.get(environment);
        // 读取不同环境下的Akka的配置
        AkkaProperties akkaProperties =
                binder.bind(
                        AKKA_PRE_ADDRESS,
                        Bindable.of(AkkaProperties.class)
                ).get();
        log.info("akkaProperties:{}", JSONObject.toJSONString(akkaProperties));
        // 1.初始化DNS配置 是否开启DNS自动刷新 在同一个网络下即可 这步其实无关痛痒 不做即可 不在同一个网络下那种场景
        // 2.创建akka种子节点
        /*
         * 3.1:如果是计算框架的话那么这里可以将自己写的一些组件注册进来 比如定义一个顶层实现类 or 用注解的方式初始化 把它当成spring中bean的加载过程就可以很好理解了 原理类似
         * 3.2:如果是业务查询那种的话 用的是另一种方式 以一个http请求贯穿actor生命周期的
         */
        // ...
    }

    @Override
    public void destroy() {
//        applicationEventPublisher.publishEvent();
        // 这里可以用毒丸 让akka停止
        log.info("Stopping server: {}", "Akka");
    }
}

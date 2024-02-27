package com.springgboot.refactor.infa.config.jwt;

import com.springgboot.refactor.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.WebApplicationContext;

import static com.springgboot.refactor.infa.config.jwt.JwtConstant.JWT_PRE_ADDRESS;

@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
        {
                JwtProperties.class
        }
)
public class JwtConfig {

    private final ConfigurableApplicationContext configurableApplicationContext;

    @Bean
    @ConditionalOnMissingBean(WebApplicationContext.class)
    public void initJwt() {
        // 获取配置文件 取当前环境
        ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();
        Binder binder = Binder.get(environment);
        // 读取不同环境下的Jwt的配置
        JwtProperties jwtProperties =
                binder.bind(
                                JWT_PRE_ADDRESS,
                                Bindable.of(JwtProperties.class))
                        .get();
        if (!jwtProperties.getEnable()) {
            log.info("JwtAuth:初始化认证未开启～");
        }
        JwtUtil.init(jwtProperties);
        log.info("JwtAuth:Jwt初始化数据认证加载完成~");
    }
}

package com.springgboot.refactor.infa.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;


/**
 * 初始化加载一些配置
 * Initializing Spring embedded WebApplicationContext 加载完成之后
 *
 * @author Elliot
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitConfig {

    private final ConfigurableApplicationContext configurableApplicationContext;

    /**
     * 启动时自动加载
     */
    @PostConstruct
    public void doSomeThing() {
    }
}

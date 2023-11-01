package com.springgboot.refactor;

import com.springgboot.refactor.util.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class SpringBootRefactorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRefactorApplication.class, args);
        log.info("SpringBootRefactorApplication Start Success ~");
    }

    /**
     * 注入上下文工具类
     */
    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
}

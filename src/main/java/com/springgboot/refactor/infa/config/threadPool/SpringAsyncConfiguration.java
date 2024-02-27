package com.springgboot.refactor.infa.config.threadPool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;

/**
 * 异步线程开启
 */
@Slf4j
public class SpringAsyncConfiguration extends AsyncConfigurerSupport {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("SpringAsyncConfiguration-{},{}", method.getName(), params, ex);
        };
    }
}

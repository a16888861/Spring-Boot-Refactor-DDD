package com.springgboot.refactor.infrastructure.config.threadPool;

/**
 * 线程池常量配置
 */
public class ThreadPoolConstant {

    public static final String THREAD_POOL_PRE_ADDRESS = "global.async.thread-pool";

    public static final String THREAD_POOL_ALL_ADDRESS = THREAD_POOL_PRE_ADDRESS + ".executors";
}

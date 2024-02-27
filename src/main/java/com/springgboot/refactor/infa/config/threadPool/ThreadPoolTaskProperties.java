package com.springgboot.refactor.infa.config.threadPool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static com.springgboot.refactor.infa.config.threadPool.ThreadPoolConstant.THREAD_POOL_PRE_ADDRESS;


/**
 * 线程池配置(每次新建一个线程池的时候在配置文件当中写一个即可)
 */
@Data
@ConfigurationProperties(prefix = THREAD_POOL_PRE_ADDRESS)
public class ThreadPoolTaskProperties {

    /**
     * 是否开启动态线程池
     */
    private boolean enable;

    /**
     * 线程池配置集合
     */
    private List<Executor> executors;

    @Data
    public static class Executor {
        /**
         * 线程池名称
         */
        private String threadPoolName;
        /**
         * 线程池名称前缀
         */
        private String threadNamePrefix = "DddThreadPool";
        /**
         * 空闲线程存活时间，单位秒
         */
        private int keepAliveSeconds = 60;
        /**
         * 核心线程数
         */
        private Integer corePoolSize = Runtime.getRuntime().availableProcessors();
        /**
         * 最大线程数
         */
        private Integer maxPoolSize = Runtime.getRuntime().availableProcessors();
        /**
         * 队列最大数量
         */
        private Integer queueCapacity = 1000;
        /**
         * 优雅关闭线程池, 执行shutdown方法(保证之前线程池中正在执行的任务和队列里的等待的任务能执行完成而不是直接丢失)
         */
        private Boolean waitForTasksToCompleteOnShutdown = true;
    }
}

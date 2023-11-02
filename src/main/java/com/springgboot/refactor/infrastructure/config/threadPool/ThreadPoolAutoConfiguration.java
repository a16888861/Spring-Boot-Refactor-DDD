package com.springgboot.refactor.infrastructure.config.threadPool;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static com.springgboot.refactor.infrastructure.config.threadPool.Constant.THREAD_POOL_ALL_ADDRESS;
import static com.springgboot.refactor.infrastructure.config.threadPool.Constant.THREAD_POOL_PRE_ADDRESS;


/**
 * 异步动态线程池装配
 */
@Slf4j
// 开启异步
@EnableAsync
// 仅在定义了THREAD_POOL_PRE_ADDRESS属性时才加载
@ConditionalOnProperty(prefix = THREAD_POOL_PRE_ADDRESS, name = "enable", havingValue = "true")
// 开启ThreadPoolTaskProperties自动配置
@EnableConfigurationProperties(
        {
                ThreadPoolTaskProperties.class
        }
)
/*
 * proxyBeanMethods = false
 * true:Full 全模式。 该模式下注入容器中的同一个组件无论被取出多少次都是同一个bean实例，即单实例对象，在该模式下SpringBoot每次启动都会判断检查容器中是否存在该组件
 * false:Lite 轻量级模式。该模式下注入容器中的同一个组件无论被取出多少次都是不同的bean实例，即多实例对象，在该模式下SpringBoot每次启动会跳过检查容器中是否存在该组件
 */
@Configuration(proxyBeanMethods = false)
// 必须在下面的类配置完成后，才会进行自动配置
@AutoConfigureBefore({TaskExecutionAutoConfiguration.class})
public class ThreadPoolAutoConfiguration implements
        BeanFactoryPostProcessor,
        ApplicationContextAware {

    private ConfigurableApplicationContext configurableApplicationContext;

    /**
     * 没有SpringAsyncConfiguration的时候创建一个(用于异常日志打印)
     *
     * @return 自定义的SpringAsyncConfiguration
     */
    @Bean
    @ConditionalOnMissingBean(SpringAsyncConfiguration.class)
    public SpringAsyncConfiguration springAsyncConfiguration() {
        return new SpringAsyncConfiguration();
    }

    /**
     * 重写SpringBean的后置处理
     *
     * @param beanFactory Bean工厂
     * @throws BeansException Bean异常
     */
    @SneakyThrows
    @Override
    public void postProcessBeanFactory(@Nonnull ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
        // 获取配置文件 取当前环境
        ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();
        Binder binder = Binder.get(environment);
        // 读取不同环境下的线程池的配置
        List<ThreadPoolTaskProperties.Executor> executors =
                binder.bind(
                                THREAD_POOL_ALL_ADDRESS,
                                Bindable.listOf(ThreadPoolTaskProperties.Executor.class))
                        .get();
        // 创建一个执行回调方法的装饰器(主要应用于传递上下文，或者提供任务的监控/统计信息) 给每个线程池配置一下(用来串traceId)
        ThreadTaskDecorator threadTaskDecorator = new ThreadTaskDecorator();
        ThreadPoolExecutor.AbortPolicy abortPolicy = new ThreadPoolExecutor.AbortPolicy();
        for (ThreadPoolTaskProperties.Executor executor : executors) {
            // 构建一个线程池
            ThreadPoolTaskExecutor threadPoolTaskExecutor = buildThreadPoolTaskExecutor(executor, abortPolicy, threadTaskDecorator);
            // 初始化线程池
            threadPoolTaskExecutor.initialize();
            // 将每个线程池进行单例注册
            beanFactory.registerSingleton(executor.getThreadPoolName(), threadPoolTaskExecutor);
        }
        log.info("动态线程池配置完成～");
    }

    /**
     * 构建线程池
     *
     * @param executor            自定义线程池配置(写在配置文件当中, 可结合配置中心使用 OR 写在库中, 开发为一个功能)
     * @param abortPolicy         拒绝策略
     * @param threadTaskDecorator 执行回调方法的装饰器，主要应用于传递上下文，或者提供任务的监控/统计信息
     * @return 构建好的线程池
     */
    private static ThreadPoolTaskExecutor buildThreadPoolTaskExecutor(
            ThreadPoolTaskProperties.Executor executor,
            ThreadPoolExecutor.AbortPolicy abortPolicy,
            ThreadTaskDecorator threadTaskDecorator) {
        // 创建一个新的线程池并设置相关参数
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(executor.getCorePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(executor.getMaxPoolSize());
        threadPoolTaskExecutor.setQueueCapacity(executor.getQueueCapacity());
        threadPoolTaskExecutor.setThreadNamePrefix(executor.getThreadNamePrefix() + "-" + executor.getThreadPoolName() + "-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(abortPolicy);
        threadPoolTaskExecutor.setTaskDecorator(threadTaskDecorator);
        threadPoolTaskExecutor.setKeepAliveSeconds(executor.getKeepAliveSeconds());
        // 是否执行线程池的shutdown方法(保证之前线程池中正在执行的任务和队列里的等待的任务能执行完成而不是直接丢失)
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(executor.getWaitForTasksToCompleteOnShutdown());
        return threadPoolTaskExecutor;
    }

    /**
     * 重新设置上下文(这个时候上下文中正常一定是有内容的)
     *
     * @param applicationContext 应用上下文
     * @throws BeansException Bean异常
     */
    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext)
            throws BeansException {
        configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
    }
}

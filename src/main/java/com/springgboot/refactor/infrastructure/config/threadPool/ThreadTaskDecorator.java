package com.springgboot.refactor.infrastructure.config.threadPool;

import com.springgboot.refactor.infrastructure.constants.BaseConstants;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

/**
 * 重写执行回调方法的装饰器，主要应用于传递上下文，或者提供任务的监控/统计信息
 */
public class ThreadTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        String traceId = MDC.get(BaseConstants.TRACE_ID);
        return (() -> {
            try {
                MDC.put(BaseConstants.TRACE_ID, traceId);
                runnable.run();
            } finally {
                MDC.clear();
            }
        });
    }
}

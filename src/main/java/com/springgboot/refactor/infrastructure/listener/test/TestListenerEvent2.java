package com.springgboot.refactor.infrastructure.listener.test;

import com.alibaba.fastjson2.JSONObject;
import com.springgboot.refactor.domain.test.event.TestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 第二种方式 - 测试监听事件
 *
 * @author Elliot
 */
@Slf4j
@Component
public class TestListenerEvent2 {

    /**
     * EventListener 默认是同步执行
     * 如果发布事件的方法处于事务中，那么事务会在监听器方法执行完毕之后才提交事件发布
     * 之后就由监听器去处理，而不要影响原有的事务
     * 也就是说希望事务及时提交我们就可以使用该注解来标识
     * 注意此注解需要spring-tx的依赖
     */
    @Async
    @EventListener({TestEvent.class})
    public void testEvent1(TestEvent event) {
        log.info("1.收到事件:{}", JSONObject.toJSONString(event));
    }

    /**
     * 使用方式如下。phase事务类型，value指定事件。
     * <p>
     * 这个注解取值有：
     * BEFORE_COMMIT(指定目标方法在事务commit之前执行)、
     * AFTER_COMMIT(指定目标方法在事务commit之后执行)、
     * AFTER_ROLLBACK(指定目标方法在事务rollback之后执行)、
     * AFTER_COMPLETION(指定目标方法在事务完成时执行，这里的完成是指无论事务是成功提交还是事务回滚了)
     * 各个值都代表什么意思表达什么功能，非常清晰，
     * 需要注意的是：
     * AFTER_COMMIT + AFTER_COMPLETION是可以同时生效的
     * AFTER_ROLLBACK + AFTER_COMPLETION是可以同时生效的
     */
    @Async(value = "testListener")
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, value = {TestEvent.class})
    public void testEvent2(TestEvent event) {
        log.info("2.收到事件 commit前:{}", JSONObject.toJSONString(event));
    }
    @Async(value = "testListener")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, value = {TestEvent.class})
    public void testEvent3(TestEvent event) {
        log.info("2.收到事件 commit后:{}", JSONObject.toJSONString(event));
    }
    @Async(value = "testListener")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION, value = {TestEvent.class})
    public void testEvent4(TestEvent event) {
        log.info("2.收到事件 事物完成时:{}", JSONObject.toJSONString(event));
    }
}

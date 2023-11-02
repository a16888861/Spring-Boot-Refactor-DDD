package com.springgboot.refactor.infrastructure.listener.test;

import com.springgboot.refactor.domain.test.event.TestEvent;
import org.springframework.context.ApplicationListener;

/**
 * 第一种方式 - 测试监听事件(不如注解方式监听执行 这样一个类代表一个事件的监听 繁琐的很)
 *
 * @author Elliot
 */
//@Component
public class TestListenerEvent1 implements ApplicationListener<TestEvent> {

    @Override
    public void onApplicationEvent(TestEvent event) {
        System.out.println("收到事件:" + event);
    }
}

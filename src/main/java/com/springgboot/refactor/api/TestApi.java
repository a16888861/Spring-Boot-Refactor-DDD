package com.springgboot.refactor.api;

import com.springgboot.refactor.api.vo.TestVo;
import com.springgboot.refactor.application.test.TestService;
import com.springgboot.refactor.domain.test.entity.TestPojo;
import com.springgboot.refactor.domain.test.event.TestEvent;
import com.springgboot.refactor.infrastructure.response.WebResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestApi {

    private final ApplicationEventPublisher applicationEventPublisher;

    public final Map<String, TestService> testServiceMap;

    @PostMapping("/testEventInterface")
    public WebResponse<Void> testEventInterface(@RequestBody TestVo testVo) {

        TestService test1 = testServiceMap.get("test1");
        test1.printSomeOne();

        TestService test2 = testServiceMap.get("test2");
        test2.printSomeOne();

        applicationEventPublisher.publishEvent(
                new TestEvent(
                        new TestPojo(
                                testVo.getTestContent()
                        )
                )
        );

        return WebResponse.success();
    }


}

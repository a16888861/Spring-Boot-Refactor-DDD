package com.springgboot.refactor.api;

import com.springgboot.refactor.api.vo.TestVo;
import com.springgboot.refactor.application.test.TestService;
import com.springgboot.refactor.domain.test.entity.TestPojo;
import com.springgboot.refactor.domain.test.event.TestEvent;
import com.springgboot.refactor.infrastructure.response.WebResponse;
import com.springgboot.refactor.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
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

    @PostMapping("/getToken")
    public WebResponse<Void> getToken() {
        String token = JwtUtil.createToken("testUser");
        log.info("getToken_token:{}", token);
        log.info("getToken_verify:{}", JwtUtil.verify(token));
        return WebResponse.success();
    }

    @PostMapping("/getToken/verify/{token}")
    public WebResponse<Void> getToken(@PathVariable("token") String token) {
        log.info("getToken_verify:{}", JwtUtil.verify(token));
        return WebResponse.success();
    }


}

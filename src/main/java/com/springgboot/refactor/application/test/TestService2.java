package com.springgboot.refactor.application.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("test2")
public class TestService2 implements TestService {
    @Override
    public void printSomeOne() {
        log.info("Test2 service");
    }
}

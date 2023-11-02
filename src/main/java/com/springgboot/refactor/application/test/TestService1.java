package com.springgboot.refactor.application.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("test1")
public class TestService1 implements TestService{
    @Override
    public void printSomeOne() {
//        printDefaultOne();
        log.info("Test1 service");
    }
}

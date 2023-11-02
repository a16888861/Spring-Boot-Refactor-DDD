package com.springgboot.refactor.application.test;

import java.util.logging.Logger;

public interface TestService {

    Logger log = Logger.getLogger("testService");

    default void printDefaultOne() {
        log.info("Test Service");
    }

    void printSomeOne();
}

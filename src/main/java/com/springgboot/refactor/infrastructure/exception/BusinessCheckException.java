package com.springgboot.refactor.infrastructure.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class BusinessCheckException extends RuntimeException {

    private int statusCode = 1;


    public BusinessCheckException(String message) {
        super(message);
    }

    public BusinessCheckException(int errorCode, String message) {
        super(message);
        this.statusCode = errorCode;
    }
}

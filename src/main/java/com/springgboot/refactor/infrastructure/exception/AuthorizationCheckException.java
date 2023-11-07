package com.springgboot.refactor.infrastructure.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class AuthorizationCheckException extends RuntimeException {

    private int statusCode = 1;


    public AuthorizationCheckException(String message) {
        super(message);
    }

    public AuthorizationCheckException(int errorCode, String message) {
        super(message);
        this.statusCode = errorCode;
    }
}

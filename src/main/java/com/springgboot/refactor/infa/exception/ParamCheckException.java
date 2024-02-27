package com.springgboot.refactor.infa.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ParamCheckException extends RuntimeException {

    private int statusCode = 1;


    public ParamCheckException(String message) {
        super(message);
    }

    public ParamCheckException(int errorCode, String message) {
        super(message);
        this.statusCode = errorCode;
    }
}

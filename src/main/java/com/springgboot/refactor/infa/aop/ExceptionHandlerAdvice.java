package com.springgboot.refactor.infa.aop;

import com.springgboot.refactor.infa.exception.AuthorizationCheckException;
import com.springgboot.refactor.infa.exception.BusinessCheckException;
import com.springgboot.refactor.infa.response.WebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 自定义全局异常
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public WebResponse<Void> handleIllegalParamException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String message = "参数不合法";
        if (errors.size() > 0) {
            message = errors.get(0).getDefaultMessage();
        }
        return WebResponse.fail(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(BindException.class)
    public WebResponse<Void> handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String message = "参数不合法";
        if (errors.size() > 0) {
            message = errors.get(0).getDefaultMessage();
        }
        return WebResponse.fail(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(BusinessCheckException.class)
    public WebResponse<Void> handleException(BusinessCheckException e) {
        log.info(e.getMessage(), e);
        return WebResponse.fail(e.getStatusCode(), e.getMessage());
    }

    @ExceptionHandler(AuthorizationCheckException.class)
    public WebResponse<Void> handleUploadFilesException(AuthorizationCheckException e) {
        return WebResponse.fail(e.getStatusCode(), e.getMessage());
    }
}

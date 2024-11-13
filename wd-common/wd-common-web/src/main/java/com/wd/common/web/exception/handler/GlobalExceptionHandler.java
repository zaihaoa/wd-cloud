package com.wd.common.web.exception.handler;

import com.wd.common.core.R;
import com.wd.common.core.exception.BusinessPromptException;
import com.wd.common.core.ResponseCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理中心点
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务提示异常
     */
    @ExceptionHandler(BusinessPromptException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> businessPrompt(BusinessPromptException e) {
        log.warn("", e);
        return R.result(ResponseCodeEnum.BUSINESS_PROMPT.getCode(), null, e.getMessage());
    }

    /**
     * 方法参数无效异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> methodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("", e);
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String message = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
        return R.result(ResponseCodeEnum.PARAM_VALID_ERROR.getCode(), null, message);
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> illegalArgument(IllegalArgumentException e) {
        log.warn("", e);
        return R.result(ResponseCodeEnum.PARAM_VALID_ERROR.getCode(), null, e.getMessage());
    }

    /**
     * 系统错误
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Object> exception(Exception e) {
        log.error("", e);
        return R.failure();
    }
}

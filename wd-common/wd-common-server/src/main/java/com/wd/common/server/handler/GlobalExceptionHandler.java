package com.wd.common.server.handler;

import cn.hutool.core.collection.CollUtil;
import com.wd.common.core.enums.ResponseCodeEnum;
import com.wd.common.core.exception.FeignRespException;
import com.wd.common.core.exception.PromptException;
import com.wd.common.core.model.R;
import com.wd.common.core.util.I18nUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理中心点
 */
@Slf4j
@RestControllerAdvice
@Order(1000000)
public class GlobalExceptionHandler {

    /**
     * 业务提示异常
     */
    @ExceptionHandler(PromptException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> prompt(PromptException e) {
        log.info("", e);
        return R.failure(e.getCode(), e.getMessage());
    }

    /**
     * 方法参数无效异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> methodArgumentNotValid(MethodArgumentNotValidException e) {
        log.info("", e);
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String message = allErrors
                .stream()
                .map(v -> I18nUtil.message(v.getDefaultMessage()))
                .distinct()
                .collect(Collectors.joining(","));
        return R.failure(ResponseCodeEnum.PARAM_VALID_ERROR.getCode(), message);
    }

    /**
     * 处理程序方法验证异常
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> methodArgumentNotValid(HandlerMethodValidationException e) {
        log.info("", e);
        List<ParameterValidationResult> parameterValidationResults = e.getParameterValidationResults();
        String message = parameterValidationResults
                .stream()
                .map(ParameterValidationResult::getResolvableErrors)
                .filter(CollUtil::isNotEmpty)
                .flatMap(Collection::stream)
                .map(MessageSourceResolvable::getDefaultMessage)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.joining(","));
        return R.failure(ResponseCodeEnum.PARAM_VALID_ERROR.getCode(), message);
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> illegalArgument(IllegalArgumentException e) {
        log.info("", e);
        return R.failure(ResponseCodeEnum.PARAM_VALID_ERROR.getCode(), e.getMessage());
    }

    /**
     * feign接口调用错误
     */
    @ExceptionHandler(FeignRespException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> feignResp(FeignRespException e) {
        log.warn("", e);
        return R.failure(e.getCode(), e.getMessage());
    }

    /**
     * 资源不存在
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<Object> noResourceFound(NoResourceFoundException e) {
        log.warn("资源不存在", e);
        return R.failure(ResponseCodeEnum.NOT_FOUND.getCode(), ResponseCodeEnum.NOT_FOUND.getPrompt());
    }

    /**
     * HTTP消息不可读异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.info("", e);
        return R.failure(ResponseCodeEnum.ERROR.getCode(), e.getMessage());
    }

    /**
     * 系统错误
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> exception(Exception e) {
        log.error("", e);
        return R.failure();
    }
}

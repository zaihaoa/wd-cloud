package com.kvc.common.satoken;

import cn.dev33.satoken.exception.NotPermissionException;
import com.wd.common.core.enums.ResponseCodeEnum;
import com.wd.common.core.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理中心点
 */
@Slf4j
@RestControllerAdvice
@Order(1000)
public class SaTokenExceptionHandler {

    /**
     * 没有权限
     */
    @ExceptionHandler(NotPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<Object> exception(NotPermissionException e) {
        log.warn("没有权限", e);
        return R.failure(ResponseCodeEnum.NOT_PERMISSION.getCode(), ResponseCodeEnum.NOT_PERMISSION.getPrompt());
    }
}

package com.wd.common.core.exception;

/**
 * 业务提示异常
 * 用于前端显示错误信息,会把message返回前端展示给用户
 *
 * @author huangwenda
 * @date 2023/2/16 18:58
 **/
public class BusinessPromptException extends RuntimeException {

    public BusinessPromptException(String message) {
        super(message);
    }

    public BusinessPromptException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

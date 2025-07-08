package com.wd.common.core.exception;

/**
 * 需要重试异常
 *
 * @author huangwenda
 * @date 2023/7/24 15:53
 **/
public class NeedRetryException extends RuntimeException {
    public NeedRetryException() {
    }

    public NeedRetryException(String message) {
        super(message);
    }
}

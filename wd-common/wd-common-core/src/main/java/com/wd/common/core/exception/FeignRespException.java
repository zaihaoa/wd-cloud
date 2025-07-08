package com.wd.common.core.exception;

import lombok.Getter;

/**
 * feign调用
 *
 * @author huangwenda
 * @date 2023/2/16 18:58
 **/
@Getter
public class FeignRespException extends RuntimeException {

    private int code;

    public FeignRespException(int code, String message) {
        super(message);
        this.code = code;
    }
}

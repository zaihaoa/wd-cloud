package com.wd.common.core.exception;

import com.wd.common.core.enums.ResponseCodeEnum;
import lombok.Getter;

/**
 * 业务提示异常
 * 用于前端显示错误信息,会把message返回前端展示给用户
 *
 * @author huangwenda
 * @date 2023/2/16 18:58
 **/
@Getter
public class PromptException extends RuntimeException {

    private int code;

    public PromptException(String message) {
        super(message);
        this.code = ResponseCodeEnum.ERROR.getCode();
    }

    public PromptException(int code, String message) {
        super(message);
        this.code = code;
    }

    public PromptException(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum.getPrompt());
        this.code = responseCodeEnum.getCode();
    }


    public PromptException(String message, Throwable throwable) {
        super(message, throwable);
        this.code = ResponseCodeEnum.ERROR.getCode();
    }
}

package com.wd.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangwenda
 * @date 2023/2/16 18:40
 **/
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum {
    SUCCESS(0, "Request Success", "Request Success"),
    NOT_LOGIN(401, "Not Login", "Not Login"),
    NOT_PERMISSION(403, "无权限", "无权限"),
    NOT_FOUND(404, "请求路径不存在", "请求路径不存在"),
    TOO_MANY_REQUESTS(429, "请求过于频繁", "请求过于频繁"),
    ERROR(500, "服务器内部错误", "系统繁忙,请稍候再试"),

    // 警告类
    BUSINESS_PROMPT(3000, "业务提示", "业务提示"),
    PARAM_VALID_ERROR(3001, "参数错误", "参数不正确"),
    VALID_ERROR_PROMPT(3003, "校验错误", "校验错误"),

    ;

    /**
     * 状态码
     */
    private final int code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 提示语
     */
    private final String prompt;
}

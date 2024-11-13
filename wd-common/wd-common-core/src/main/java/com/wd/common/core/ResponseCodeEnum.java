package com.wd.common.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangwenda
 * @date 2023/2/16 18:40
 **/
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum {
    SUCCESS(0, "请求成功", "请求成功"),
    TIME_OUT(403, "请求超时", "请求超时,请稍候再试"),
    TOO_MANY_REQUESTS(429, "请求过于频繁", "请求过于频繁,请稍候再试"),
    ERROR(500, "服务器内部错误", "系统繁忙,请稍候再试"),

    // 警告类
    BUSINESS_PROMPT(3000, "业务提示", "业务提示"),
    PARAM_VALID_ERROR(3001, "参数错误", "参数不正确"),
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

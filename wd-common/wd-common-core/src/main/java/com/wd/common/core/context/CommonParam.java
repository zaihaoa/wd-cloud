package com.wd.common.core.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author huangwenda
 * @date 2023/9/14 18:17
 **/
@Getter
@Setter
@ToString
public class CommonParam {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户登录名
     */
    private String userLoginName;
    /**
     * 用户姓名
     */
    private String userRealName;

    /**
     * traceId
     */
    private String traceId;

    /**
     * traceExtra
     */
    private String traceExtra;

    /**
     * token
     */
    private String token;
}

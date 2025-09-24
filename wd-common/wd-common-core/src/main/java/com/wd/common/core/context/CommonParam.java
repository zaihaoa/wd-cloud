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
     * traceId
     */
    private String traceId;

    /**
     * traceExtra
     */
    private String traceExtra;

    /**
     * 请求IP
     */
    private String requestIp;

    /**
     * 用户id
     */
    private Long userId;
}

package com.wd.common.core.constants;

/**
 * 系统常量
 *
 * @author huangwenda
 * @date 2023/2/15 10:21
 **/
public class CommonConstant {

    /**
     * 本地环境
     */
    public static final String PROFILE_LOCAL = "local";
    /**
     * 开发环境
     */
    public static final String PROFILE_DEV = "dev";
    /**
     * 测试环境
     */
    public static final String PROFILE_TEST = "test";
    /**
     * 预发布环境
     */
    public static final String PROFILE_PRE = "pre";
    /**
     * 生产环境
     */
    public static final String PROFILE_PROD = "prod";

    /**
     * 系统用户ID
     */
    public static final long SYSTEM_USER_ID = 0L;
    /**
     * 系统用户名称
     */
    public static final String SYSTEM_USER_NAME = "System";

    /**
     * 默认值0
     */
    public static final Long ZERO_LONG = 0L;

    /**
     * 默认值0
     */
    public static final Integer ZERO_INTEGER = 0;

    /**
     * 逻辑删除字段未删除标记
     */
    public static final long NOT_DELETE_FLAG = 0L;

    /**
     * 没有权限标记
     */
    public static final Long NOT_PERMISSION_FLAG = -1L;


    /**
     * 请求头（traceId）
     */
    public static final String HEADER_TRACE_ID = "W-Trace-Id";
    /**
     * 请求头（traceExtra）
     */
    public static final String HEADER_TRACE_EXTRA = "W-Trace-Extra";
    /**
     * 请求头（请求id）
     */
    public static final String HEADER_REQUEST_IP = "W-Request-Ip";
    /**
     * 请求头（token）
     */
    public static final String HEADER_TOKEN = "W-Token";
    /**
     * 请求头（用户id）
     */
    public static final String HEADER_USER_ID = "W-User-Id";


    /**
     * traceId
     */
    public static final String TRACE_ID = "traceId";
    /**
     * traceExtra
     */
    public static final String TRACE_EXTRA = "traceExtra";
    /**
     * 请求IP
     */
    public static final String REQUEST_IP = "requestIp";
    /**
     * token
     */
    public static final String TOKEN = "token";
    /**
     * 用户id
     */
    public static final String USER_ID = "userId";

}

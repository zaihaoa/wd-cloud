package com.wd.common.core.constants;

/**
 * 系统常量
 *
 * @author huangwenda
 * @date 2023/2/15 10:21
 **/
public class CommonConstant {

    /**
     * 开发环境
     */
    public static final String PROFILE_DEV = "dev";
    /**
     * 测试环境
     */
    public static final String PROFILE_TEST = "test";
    /**
     * 生产环境
     */
    public static final String PROFILE_PROD = "prod";


    /**
     * 请求头（用户id）
     */
    public static final String COMMON_PARAM_CONTENT = "commonParamContent";

    /**
     * 系统用户ID
     */
    public static final Long SYSTEM_USER_ID = 0L;
    /**
     * 系统用户真实名称
     */
    public static final String SYSTEM_USER_REAL_NAME = "System";


    /**
     * 请求头（用户id）
     */
    public static final String HEADER_USER_ID = "w-user-id";
    /**
     * 请求头（用户登录名）
     */
    public static final String HEADER_USER_LOGIN_NAME = "w-user-login-name";
    /**
     * 请求头（用户真实名称）
     */
    public static final String HEADER_USER_REAL_NAME = "w-user-real-name";
    /**
     * 请求头（traceId）
     */
    public static final String HEADER_TRACE_ID = "w-trace-id";
    /**
     * 请求头（token）
     */
    public static final String HEADER_TOKEN = "w-token";
    /**
     * 用户id
     */
    public static final String USER_ID = "userId";
    /**
     * 用户登录名
     */
    public static final String USER_LOGIN_NAME = "userLoginName";
    /**
     * 用户姓名
     */
    public static final String USER_REAL_NAME = "userRealName";

    /**
     * traceId
     */
    public static final String TRACE_ID = "traceId";
    /**
     * traceExtra
     */
    public static final String TRACE_EXTRA = "traceExtra";
    /**
     * token
     */
    public static final String TOKEN = "token";
}

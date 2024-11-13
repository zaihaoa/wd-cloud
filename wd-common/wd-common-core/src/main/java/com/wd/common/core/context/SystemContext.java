package com.wd.common.core.context;

/**
 * @author huangwenda
 * @date 2023/2/15 10:20
 **/
public class SystemContext {
    private static ThreadLocal<CommonParam> COMMON_PARAM_CONTEXT = new ThreadLocal<>();

    static {
        COMMON_PARAM_CONTEXT.set(new CommonParam());
    }

    public static CommonParam get() {
        return COMMON_PARAM_CONTEXT.get();
    }

    public static void set(CommonParam context) {
        COMMON_PARAM_CONTEXT.set(context);
    }

    public static Long getUserId() {
        return COMMON_PARAM_CONTEXT.get().getUserId();
    }

    public static String getUserLoginName() {
        return COMMON_PARAM_CONTEXT.get().getUserLoginName();
    }

    public static String getUserRealName() {
        return COMMON_PARAM_CONTEXT.get().getUserRealName();
    }

    public static String getTraceId() {
        return COMMON_PARAM_CONTEXT.get().getTraceId();
    }
}

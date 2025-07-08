package com.wd.common.core.context;


import com.wd.common.core.exception.PromptException;

/**
 * @author huangwenda
 * @date 2023/2/15 10:20
 **/
public class SystemContext {
    private static ThreadLocal<CommonParam> COMMON_PARAM_CONTEXT = new ThreadLocal<>();

    public static CommonParam get() {
        CommonParam commonParam = COMMON_PARAM_CONTEXT.get();
        if (commonParam == null) {
            COMMON_PARAM_CONTEXT.set(new CommonParam());
        }
        return COMMON_PARAM_CONTEXT.get();
    }

    public static void set(CommonParam context) {
        COMMON_PARAM_CONTEXT.set(context);
    }

    public static Long getUserId() {
        return get().getUserId();
    }

    public static Long getUserIdAssertExist() {
        Long userId = getUserId();
        if (userId == null) {
            throw new PromptException("userId为空");
        }
        return userId;
    }

    public static String getTraceId() {
        return get().getTraceId();
    }

    public static String getTraceExtra() {
        return get().getTraceExtra();
    }

    public static void clear() {
        COMMON_PARAM_CONTEXT.remove();
    }
}

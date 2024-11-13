package com.wd.common.core.util;


import com.wd.common.core.CommonResponse;
import com.wd.common.core.ResponseCodeEnum;
import com.wd.common.core.annotions.Nullable;

import java.util.Optional;

/**
 * @author huangwenda
 * @date 2023/8/17 16:54
 **/
@SuppressWarnings("rawtypes")
public class ResponseUtil {

    /**
     * 判断响应是否成功
     * code=0 代表成功,否则认为失败
     *
     * @param r 通用返回对象
     * @return 响应是否成功
     */
    public static boolean isSuccess(@Nullable CommonResponse r) {
        return r != null && ResponseCodeEnum.SUCCESS.getCode() == r.getCode();
    }

    /**
     * 判断响应是否失败
     * code=0 代表成功,否则认为失败
     *
     * @param r 通用返回对象
     * @return 响应是否失败
     */
    public static boolean isFailure(@Nullable CommonResponse r) {
        return !isSuccess(r);
    }

    /**
     * 获取message提示信息
     *
     * @param r 通用返回对象
     * @return message提示信息
     */
    public static String safeGetMessage(@Nullable CommonResponse r) {
        return Optional.ofNullable(r).map(CommonResponse::getMessage).orElse("");
    }

    public static void assertSuccess(@Nullable CommonResponse r) {
        assertSuccess(r, null);
    }

    /**
     * 断言响应成功
     *
     * @param r       通用返回对象
     * @param message 异常信息
     */
    public static void assertSuccess(@Nullable CommonResponse r, @Nullable String message) {
        if (isFailure(r)) {
            throw new IllegalStateException(message);
        }
    }
}

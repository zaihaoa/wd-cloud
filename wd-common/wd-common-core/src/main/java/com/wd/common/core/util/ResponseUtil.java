package com.wd.common.core.util;



import com.alibaba.fastjson2.JSON;
import com.wd.common.core.annotions.Nullable;
import com.wd.common.core.enums.ResponseCodeEnum;
import com.wd.common.core.exception.FeignRespException;
import com.wd.common.core.model.R;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

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
    public static boolean isSuccess(@Nullable R r) {
        return r != null && ResponseCodeEnum.SUCCESS.getCode() == r.getCode();
    }

    /**
     * 判断响应是否失败
     * code=0 代表成功,否则认为失败
     *
     * @param r 通用返回对象
     * @return 响应是否失败
     */
    public static boolean isFailure(@Nullable R r) {
        return !isSuccess(r);
    }

    /**
     * 获取message提示信息
     *
     * @param r 通用返回对象
     * @return message提示信息
     */
    public static String safeGetMessage(@Nullable R r) {
        return Optional.ofNullable(r).map(R::getMessage).orElse("");
    }

    public static void assertSuccess(@Nullable R r) {
        assertSuccess(r, null);
    }

    /**
     * 断言响应成功
     *
     * @param r       通用返回对象
     * @param message 异常信息
     */
    public static void assertSuccess(@Nullable R r, @Nullable String message) {
        if (isFailure(r)) {
            throw new IllegalStateException(message);
        }
    }

    public static void assertSuccessPrompt(@Nullable R r, String message) {
        if (isFailure(r)) {
            String prompt = Optional.ofNullable(message).orElse("请求失败");
            if (Objects.isNull(r)) {
                throw new IllegalStateException(String.format("%s,返回对象为空", prompt));
            }
            throw new IllegalStateException(String.format("%s,状态码:%s,提示信息:%s,数据:%s",
                    prompt,
                    r.getCode(),
                    r.getMessage(),
                    JSON.toJSONString(r.getData())));
        }
    }

    /**
     * 断言响应成功
     *
     * @param r       通用返回对象
     * @param messageSupplier 异常信息提供者
     */
    public static void assertSuccessSupplier(@Nullable R r, Supplier<String> messageSupplier) {
        if (isFailure(r)) {
            throw new IllegalStateException(nullSafeGet(messageSupplier));
        }
    }

    private static String nullSafeGet(@Nullable Supplier<String> messageSupplier) {
        return messageSupplier != null ? messageSupplier.get() : null;
    }

    /**
     * 断言响应成功
     *
     * @param r       通用返回对象
     */
    public static <T> T assertSuccessGetData(@Nullable R<T> r) {
        if (isSuccess(r)) {
            return r.getData();
        }
        throw new FeignRespException(r.getCode(), r.getMessage());
    }
}

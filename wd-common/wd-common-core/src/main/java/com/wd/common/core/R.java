package com.wd.common.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author huangwenda
 * @date 2023/1/4 15:52
 **/
@Getter
@Setter
@ToString
@SuppressWarnings("all")
@Schema(description = "通用返回对象")
public class R<T> implements CommonResponse<T>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * {@link ResponseCodeEnum}
     */
    @Schema(description = "状态码（状态码等于0为成功）", defaultValue = "0", allowableValues = {"0", "500"})
    private int code;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "提示消息")
    private String message;

    private R() {
    }

    public R(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <E> R<E> result(int code, E data, String message) {
        return new R<>(code, data, message);
    }

    public static <E> R<E> success() {
        return new R<>(ResponseCodeEnum.SUCCESS.getCode(), null, ResponseCodeEnum.SUCCESS.getPrompt());
    }

    public static <E> R<E> success(E data) {
        return new R<>(ResponseCodeEnum.SUCCESS.getCode(), data, ResponseCodeEnum.SUCCESS.getPrompt());
    }

    public static <E> R<E> success(E data, String message) {
        return new R<>(ResponseCodeEnum.SUCCESS.getCode(), data, message);
    }

    public static <E> R<E> failure() {
        return failure(ResponseCodeEnum.ERROR.getCode(), ResponseCodeEnum.ERROR.getPrompt());
    }

    public static <E> R<E> failure(String message) {
        return failure(ResponseCodeEnum.ERROR.getCode(), message);
    }

    public static <E> R<E> failure(int code, String message) {
        return new R<>(code, null, message);
    }

    public static <E> R<E> failure(E data, String message) {
        return new R<>(ResponseCodeEnum.ERROR.getCode(), data, message);
    }
}

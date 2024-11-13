package com.wd.common.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author huangwenda
 * @date 2023/1/4 15:52
 **/
@Getter
@Setter
@ToString
@Accessors(chain = true)
@SuppressWarnings("all")
@Schema(description = "RPC接口返回对象")
public class RpcResponse<T> implements CommonResponse<T>, Serializable {
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


    private RpcResponse() {
    }

    public RpcResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <E> RpcResponse<E> result(int code, E data, String message) {
        return new RpcResponse<>(code, data, message);
    }

    public static <E> RpcResponse<E> success() {
        return new RpcResponse<>(ResponseCodeEnum.SUCCESS.getCode(), null, ResponseCodeEnum.SUCCESS.getPrompt());
    }

    public static <E> RpcResponse<E> success(E data) {
        return new RpcResponse<>(ResponseCodeEnum.SUCCESS.getCode(), data, ResponseCodeEnum.SUCCESS.getPrompt());
    }

    public static <E> RpcResponse<E> success(E data, String message) {
        return new RpcResponse<>(ResponseCodeEnum.SUCCESS.getCode(), data, message);
    }

    public static <E> RpcResponse<E> failure() {
        return failure(ResponseCodeEnum.ERROR.getCode(), ResponseCodeEnum.ERROR.getPrompt());
    }

    public static <E> RpcResponse<E> failure(String message) {
        return failure(ResponseCodeEnum.ERROR.getCode(), message);
    }

    public static <E> RpcResponse<E> failure(int code, String message) {
        return new RpcResponse<>(code, null, message);
    }

    public static <E> RpcResponse<E> failure(E data, String message) {
        return new RpcResponse<>(ResponseCodeEnum.ERROR.getCode(), data, message);
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.wd.common.core;

/**
 * @author huangwenda
 * @date 2023/9/8 18:11
 **/
public interface CommonResponse<T> {

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    int getCode();

    /**
     * 设置状态码
     */
    void setCode(int code);

    /**
     * 获取数据
     *
     * @return 数据
     */
    T getData();

    /**
     * 设置数据
     */
    void setData(T data);

    /**
     * 获取提示信息
     *
     * @return 提示信息
     */
    String getMessage();

    /**
     * 设置提示信息
     */
    void setMessage(String message);
}

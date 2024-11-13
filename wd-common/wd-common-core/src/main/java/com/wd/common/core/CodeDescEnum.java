package com.wd.common.core;

/**
 * @author huangwenda
 * @date 2023/3/3 15:57
 **/
public interface CodeDescEnum<T> {

    /**
     * 获取key
     * @return key值
     */
    T getCode();

    /**
     * 获取描述
     * @return 描述值
     */
    String getDesc();
}

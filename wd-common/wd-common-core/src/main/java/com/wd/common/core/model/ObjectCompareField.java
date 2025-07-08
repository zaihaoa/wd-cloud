package com.wd.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author huangwenda
 * @date 2023/3/8 14:17
 **/
@Schema(description = "对象比较字段")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ObjectCompareField {
    /**
     * 字段名
     */
    private String name;

    /**
     * 字段值
     */
    private String value;

    /**
     * 原始字段值
     */
    private Object originValue;

    /**
     * 注解字段名称
     */
    private String annotationName;
}

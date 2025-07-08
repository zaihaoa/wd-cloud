package com.wd.common.core.model;

import lombok.*;

/**
 * @author huangwenda
 * @date 2023/3/8 14:17
 **/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ObjectCompareResult {
    /**
     * 字段名称
     */
    private String name;
    /**
     * 老的值
     */
    private String oldValue;
    /**
     * 新的值
     */
    private String newValue;
}

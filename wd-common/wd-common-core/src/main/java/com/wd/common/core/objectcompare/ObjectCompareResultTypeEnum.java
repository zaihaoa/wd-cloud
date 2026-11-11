package com.wd.common.core.objectcompare;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对象比较log输出格式类型
 * @author huangwenda
 * @date 2026/11/11 16:15
 **/
@Getter
@AllArgsConstructor
public enum ObjectCompareResultTypeEnum {
    STRING("STRING", "字符串"),
    COLLECTION("COLLECTION", "集合"),
    ;

    /**
     * 类型
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

}

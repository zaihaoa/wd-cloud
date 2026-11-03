package com.wd.common.core.annotions;

import java.lang.annotation.*;

/**
 * Apifox接口标签
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface ApifoxTags {

    /**
     * 目录名称
     */
    String value();

}

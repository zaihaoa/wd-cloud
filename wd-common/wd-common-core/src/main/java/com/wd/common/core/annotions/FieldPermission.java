package com.wd.common.core.annotions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段权限注解
 *
 * @author huangwenda
 * @date 2026/11/4 15:17
 **/
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldPermission {

    /**
     * 字段权限编码
     */
    String value();
}

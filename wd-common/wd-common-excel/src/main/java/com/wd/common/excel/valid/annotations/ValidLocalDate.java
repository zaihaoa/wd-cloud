package com.wd.common.excel.valid.annotations;

import java.lang.annotation.*;


@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLocalDate {

    /**
     * 字段名
     */
    String value() default "";

    /**
     * 是否不能为空
     */
    boolean notNull() default false;

    /**
     * 是否忽略,该注解不生效
     */
    boolean ignore() default false;
}

package com.wd.common.excel.valid.annotations;

import java.lang.annotation.*;


@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInt {

    /**
     * 字段名
     */
    String value() default "";

    /**
     * 是否不能为空
     */
    boolean notNull() default false;

    /**
     * 小于
     */
    int less() default Integer.MAX_VALUE;

    /**
     * 小于等于
     */
    int lessOrEqual() default Integer.MAX_VALUE;

    /**
     * 大于
     */
    int greater() default Integer.MIN_VALUE;

    /**
     * 大于等于
     */
    int greaterOrEqual() default Integer.MIN_VALUE;

    /**
     * 是否忽略,该注解不生效
     */
    boolean ignore() default false;
}

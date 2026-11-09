package com.wd.common.excel.valid.annotations;

import java.lang.annotation.*;


@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidString {

    /**
     * 字段名
     */
    String value() default "";

    /**
     * 是否不能为空
     */
    boolean notBlank() default false;

    /**
     * 字符串最小长度,包含minLength
     */
    int minLength() default 0;

    /**
     * 字符串最大长度,包含maxLength
     */
    int maxLength() default Integer.MAX_VALUE;

    /**
     * 允许的值
     */
    String[] allowableValues() default {};

    /**
     * 是否忽略,该注解不生效
     */
    boolean ignore() default false;
}

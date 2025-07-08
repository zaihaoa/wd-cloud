package com.wd.common.core.annotions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huangwenda
 * @date 2023/3/8 14:17
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectCompare {
    /**
     * 字段名称
     */
    String value();

    /**
     * 是否忽略该值（不参与比较）
     */
    boolean ignore() default false;
}

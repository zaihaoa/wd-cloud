package com.wd.common.core.annotions;

import java.lang.annotation.*;

/**
 * 不能为null
 * @author huangwenda
 * @date 2023/8/8 18:13
 **/
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER,ElementType.LOCAL_VARIABLE})
public @interface NotNull {
}

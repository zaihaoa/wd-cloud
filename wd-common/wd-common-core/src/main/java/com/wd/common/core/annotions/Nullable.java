package com.wd.common.core.annotions;

import java.lang.annotation.*;

/**
 * 可以为null
 * @author huangwenda
 * @date 2023/8/8 18:14
 **/
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER,ElementType.LOCAL_VARIABLE})
public @interface Nullable {
}

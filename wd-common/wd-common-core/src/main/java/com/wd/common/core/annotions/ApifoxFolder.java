package com.wd.common.core.annotions;

import java.lang.annotation.*;

/**
 * Apifox文件夹目录名称
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface ApifoxFolder {

    /**
     * 目录名称
     */
    String value();

}

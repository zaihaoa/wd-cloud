package com.wd.common.core.annotions;

import java.lang.annotation.*;

/**
 * 文件夹目录名称
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Folder {

    /**
     * 目录名称
     */
    String value();

}

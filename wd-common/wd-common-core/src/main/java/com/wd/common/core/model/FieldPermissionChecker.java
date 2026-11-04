package com.wd.common.core.model;

/**
 *
 *
 * @author huangwenda
 * @date 2026/11/4 15:18
 **/
@FunctionalInterface
public interface FieldPermissionChecker {

    /**
     * 是否有权限
     */
    boolean hasPermission(String code);
}

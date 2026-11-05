package com.wd.system.basic.service;

import com.wd.system.system.api.basic.dto.UserPermissionDTO;

/**
 *
 *
 * @author huangwenda
 * @date 2026/11/2 15:43
 **/
public interface PermissionService {

    /**
     * 获取用户权限
     * @param userId 用户ID
     * @return 用户权限
     */
    UserPermissionDTO getUserPermission(Long userId);

    /**
     * 获取当前登录用户权限
     * @return 用户权限
     */
    UserPermissionDTO getCurrentUserPermission();

}

package com.wd.system.basic.service;

import com.wd.common.core.model.R;
import com.wd.system.system.api.basic.dto.UserPermissionDTO;
import io.swagger.v3.oas.annotations.Operation;

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
    R<UserPermissionDTO> getUserPermission(Long userId);

    /**
     * 获取当前登录用户权限
     * @return 用户权限
     */
    @Operation(summary = "获取当前用户权限")
    R<UserPermissionDTO> getCurrentUserPermission();

}

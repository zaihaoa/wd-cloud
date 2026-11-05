package com.wd.system.basic.service.impl;

import com.wd.common.core.context.SystemContext;
import com.wd.common.core.model.R;
import com.wd.system.basic.service.PermissionService;
import com.wd.system.system.api.basic.dto.UserPermissionDTO;

/**
 *
 *
 * @author huangwenda
 * @date 2026/11/2 15:44
 **/
public class PermissionServiceImpl implements PermissionService {

    @Override
    public R<UserPermissionDTO> getUserPermission(Long userId) {
        return null;
    }

    @Override
    public R<UserPermissionDTO> getCurrentUserPermission() {
        return getUserPermission(SystemContext.getUserIdAssertExist());
    }
}

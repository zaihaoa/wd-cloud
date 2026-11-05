package com.wd.system.basic.controller.feign;

import com.wd.common.core.annotions.ApifoxFolder;
import com.wd.common.core.model.R;
import com.wd.system.basic.service.PermissionService;
import com.wd.system.system.api.basic.dto.UserPermissionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@ApifoxFolder("权限/feign接口")
@Tag(name = "权限-feign接口")
@RestController
@RequestMapping("/feign/permission")
public class PermissionFeignController {

    @Resource
    private PermissionService permissionService;

    @Operation(summary = "获取用户权限")
    @GetMapping("/get-user-permission")
    public R<UserPermissionDTO> getUserPermission(@RequestParam("userId") Long userId) {
        return R.success(permissionService.getUserPermission(userId));
    }

    @Operation(summary = "获取当前用户权限")
    @GetMapping("/get-current-user-permission")
    public R<UserPermissionDTO> getCurrentUserPermission() {
        return R.success(permissionService.getCurrentUserPermission());
    }
}

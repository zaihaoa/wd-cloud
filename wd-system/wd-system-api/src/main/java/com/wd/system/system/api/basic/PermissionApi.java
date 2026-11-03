package com.wd.system.system.api.basic;

import com.wd.common.core.model.R;
import com.wd.system.system.api.basic.dto.UserPermissionDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author huangwenda
 * @date 2024/1/25 17:55
 **/
@FeignClient(
        name = "system-server",
        contextId = "permissionApi",
        path = "/feign/permission")
public interface PermissionApi {

    @Operation(summary = "获取用户权限")
    @GetMapping("/get-user-permission")
    R<UserPermissionDTO> getUserPermission(@RequestParam("userId") Long userId);

    @Operation(summary = "获取当前用户权限")
    @GetMapping("/get-current-user-permission")
    R<UserPermissionDTO> getCurrentUserPermission();
}

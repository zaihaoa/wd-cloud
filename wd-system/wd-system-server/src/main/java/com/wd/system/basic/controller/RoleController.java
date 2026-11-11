package com.wd.system.basic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.wd.common.core.annotions.ApifoxFolder;
import com.wd.common.core.constants.PermissionConstant;
import com.wd.common.core.enums.FieldPermissionEnum;
import com.wd.common.core.model.*;
import com.wd.system.basic.domain.dto.RoleAddDTO;
import com.wd.system.basic.domain.dto.RoleAuthorizeAllDTO;
import com.wd.system.basic.domain.dto.RoleAuthorizeDTO;
import com.wd.system.basic.domain.dto.RolePageParamDTO;
import com.wd.system.basic.domain.dto.RoleUpdateDTO;
import com.wd.system.basic.domain.dto.RoleUpdateStatusDTO;
import com.wd.system.basic.domain.vo.FieldPermissionGroupVO;
import com.wd.system.basic.domain.vo.FieldPermissionItemVO;
import com.wd.system.basic.domain.vo.RolePageVO;
import com.wd.system.basic.domain.vo.RoleDetailVO;
import com.wd.system.basic.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色 前端控制器
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@ApifoxFolder("角色")
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @Operation(summary = "列表查询")
    @SaCheckPermission(PermissionConstant.ROLE_LIST)
    @PostMapping("/page")
    public R<PageInfo<RolePageVO>> pageList(@RequestBody @Valid RolePageParamDTO pageParam) {
        return R.success(roleService.pageList(pageParam));
    }

    @Operation(summary = "新增角色")
    @SaCheckPermission(PermissionConstant.ROLE_ADD)
    @PostMapping("/add")
    public R<Long> addRole(@RequestBody @Valid RoleAddDTO roleAddParam) {
        return R.success(roleService.addRole(roleAddParam));
    }

    @Operation(summary = "更新角色")
    @SaCheckPermission(PermissionConstant.ROLE_UPDATE)
    @PutMapping("/update")
    public R<Boolean> updateRole(@RequestBody @Valid RoleUpdateDTO roleUpdateParam) {
        return R.success(roleService.updateRole(roleUpdateParam));
    }

    @Operation(summary = "启用禁用")
    @SaCheckPermission(PermissionConstant.ROLE_UPDATE)
    @PostMapping("/update-status")
    public R<Boolean> updateStatus(@RequestBody @Valid RoleUpdateStatusDTO updateStatusDTO) {
        return R.success(roleService.updateStatus(updateStatusDTO));
    }

    @Operation(summary = "删除角色")
    @SaCheckPermission(PermissionConstant.ROLE_DELETE)
    @DeleteMapping("/delete")
    public R<Boolean> deleteRoleById(@RequestBody @Valid IdDTO id) {
        return R.success(roleService.deleteRoleById(id));
    }

    @Operation(summary = "获取角色详情及授权")
    @SaCheckPermission(PermissionConstant.ROLE_LIST)
    @GetMapping("/detail")
    public R<RoleDetailVO> detail(@RequestParam("id") Long id) {
        return R.success(roleService.detail(id));
    }

    @Operation(summary = "授权或取消角色全部权限标记")
    @SaCheckPermission(PermissionConstant.ROLE_AUTH)
    @PutMapping("/authorize-all")
    public R<Boolean> authorizeAll(@RequestBody @Valid RoleAuthorizeAllDTO dto) {
        return R.success(roleService.authorizeAll(dto));
    }

    @Operation(summary = "保存角色指定类型的授权")
    @SaCheckPermission(PermissionConstant.ROLE_AUTH)
    @PutMapping("/authorize")
    public R<Boolean> authorize(@RequestBody @Valid RoleAuthorizeDTO dto) {
        return R.success(roleService.authorize(dto));
    }

    @Operation(summary = "角色下拉框")
    @GetMapping("/role-combobox")
    public R<List<SelectCombobox>> roleCombobox(@RequestParam(value = "status", required = false)
                                                @Parameter(name = "status", description = "状态,不传默认所有(ENABLED:启用,DISABLED:禁用)")
                                                String status) {
        return R.success(roleService.roleCombobox(status));
    }

    @Operation(summary = "获取所有字段权限常量")
    @GetMapping("/field-permission-constants")
    public R<List<FieldPermissionGroupVO>> fieldPermissionConstants() {
        return R.success(roleService.fieldPermissionConstants());
    }

}

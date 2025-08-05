package com.wd.system.basic.controller;

import com.wd.common.core.model.Id;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.model.R;
import com.wd.system.basic.domain.dto.RoleAddDTO;
import com.wd.system.basic.domain.dto.RolePageParamDTO;
import com.wd.system.basic.domain.dto.RoleUpdateDTO;
import com.wd.system.basic.domain.vo.RolePageVO;
import com.wd.system.basic.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 角色 前端控制器
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @Operation(summary = "列表查询")
    @PostMapping("/page")
    public R<PageInfo<RolePageVO>> pageList(@RequestBody RolePageParamDTO pageParam) {
        return R.success(roleService.pageList(pageParam));
    }

    @Operation(summary = "新增角色")
    @PostMapping("/add")
    public R<Long> addRole(@RequestBody @Valid RoleAddDTO roleAddParam) {
        return R.success(roleService.addRole(roleAddParam));
    }

    @Operation(summary = "更新角色")
    @PutMapping("/update")
    public R<Boolean> updateRole(@RequestBody @Valid RoleUpdateDTO roleUpdateParam) {
        return R.success(roleService.updateRole(roleUpdateParam));
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/delete")
    public R<Boolean> deleteRoleById(@RequestBody @Valid Id id) {
        return R.success(roleService.deleteRoleById(id));
    }

}

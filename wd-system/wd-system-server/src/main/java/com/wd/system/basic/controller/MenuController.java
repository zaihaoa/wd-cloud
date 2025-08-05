package com.wd.system.basic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.wd.common.core.constants.PermissionConstant;
import com.wd.common.core.model.Id;
import com.wd.common.core.model.R;
import com.wd.system.basic.domain.dto.MenuAddDTO;
import com.wd.system.basic.domain.dto.MenuUpdateDTO;
import com.wd.system.basic.domain.vo.MenuDetailVO;
import com.wd.system.basic.domain.vo.MenuTreeVO;
import com.wd.system.basic.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    @Operation(summary = "新增菜单")
    @SaCheckPermission(PermissionConstant.MENU_ADD)
    @PostMapping("/add")
    public R<Long> addMenu(@RequestBody @Valid MenuAddDTO menuAddDTO) {
        return R.success(menuService.addMenu(menuAddDTO));
    }

    @Operation(summary = "更新菜单")
    @SaCheckPermission(PermissionConstant.MENU_UPDATE)
    @PutMapping("/update")
    public R<Boolean> updateMenu(@RequestBody @Valid MenuUpdateDTO menuUpdateDTO) {
        return R.success(menuService.updateMenu(menuUpdateDTO));
    }

    @Operation(summary = "删除菜单")
    @SaCheckPermission(PermissionConstant.MENU_DELETE)
    @DeleteMapping("/delete")
    public R<Boolean> deleteMenuById(@RequestBody @Valid Id idParam) {
        return R.success(menuService.deleteMenuById(idParam));
    }

    @Operation(summary = "所有菜单列表Tree(过滤用户权限)")
    @GetMapping("/user-permission-menu-tree")
    public R<List<MenuTreeVO>> userPermissionMenuTree() {
        return R.success(menuService.userPermissionMenuTree());
    }


    @Operation(summary = "所有菜单列表Tree")
    @GetMapping("/menu-tree")
    public R<List<MenuTreeVO>> menuTree() {
        return R.success(menuService.menuTree());
    }

    @Operation(summary = "获取详情")
    @PostMapping("/detail")
    public R<MenuDetailVO> detail(@RequestBody @Valid Id idParam) {
        return R.success(menuService.detail(idParam));
    }

}

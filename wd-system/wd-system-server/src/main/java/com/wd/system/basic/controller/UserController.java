package com.wd.system.basic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.wd.common.core.annotions.ApifoxFolder;
import com.wd.common.core.constants.PermissionConstant;
import com.wd.common.core.model.IdDTO;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.model.R;
import com.wd.common.core.model.SelectCombobox;
import com.wd.system.basic.domain.dto.*;
import com.wd.system.basic.domain.vo.UserAuthorizePageVO;
import com.wd.system.basic.domain.vo.UserDetailVO;
import com.wd.system.basic.domain.vo.UserInfoVO;
import com.wd.system.basic.domain.vo.UserPageVO;
import com.wd.system.basic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@ApifoxFolder("用户")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Operation(summary = "列表查询")
    @SaCheckPermission(PermissionConstant.USER_LIST)
    @PostMapping("/page")
    public R<PageInfo<UserPageVO>> pageList(@RequestBody UserPageParamDTO pageParam) {
        return R.success(userService.pageList(pageParam));
    }

    @Operation(summary = "授权用户分页查询")
    @PostMapping("/authorize-page")
    public R<PageInfo<UserAuthorizePageVO>> authorizePage(@RequestBody @Valid UserAuthorizePageParamDTO pageParam) {
        return R.success(userService.authorizePage(pageParam));
    }

    @Operation(summary = "新增用户")
    @SaCheckPermission(PermissionConstant.USER_ADD)
    @PostMapping("/add")
    public R<Long> addUser(@RequestBody @Valid UserAddDTO userAddParam) {
        return R.success(userService.addUser(userAddParam));
    }

    @Operation(summary = "更新用户")
    @SaCheckPermission(PermissionConstant.USER_UPDATE)
    @PutMapping("/update")
    public R<Boolean> updateUser(@RequestBody @Valid UserUpdateDTO userUpdateParam) {
        return R.success(userService.updateUser(userUpdateParam));
    }

    @Operation(summary = "删除用户")
    @SaCheckPermission(PermissionConstant.USER_DELETE)
    @DeleteMapping("/delete")
    public R<Boolean> deleteUserById(@RequestBody @Valid IdDTO id) {
        return R.success(userService.deleteUserById(id));
    }

    @Operation(summary = "启用禁用")
    @SaCheckPermission(PermissionConstant.USER_UPDATE)
    @PostMapping("/update-status")
    public R<Boolean> updateStatus(@RequestBody @Valid UserUpdateStatusDTO updateStatusDTO) {
        return R.success(userService.updateStatus(updateStatusDTO));
    }

    @Operation(summary = "修改密码")
    @PostMapping("/update-password")
    public R<Boolean> updatePassword(@RequestBody @Valid UserUpdatePasswordDTO userUpdatePasswordDTO) {
        return R.success(userService.updatePassword(userUpdatePasswordDTO));
    }

    @Operation(summary = "重置密码")
    @SaCheckPermission(value = PermissionConstant.USER_RESET_PASSWORD)
    @PostMapping("/reset-password")
    public R<Boolean> resetPassword(@RequestBody @Valid UserResetPasswordDTO userResetPasswordDTO) {
        return R.success(userService.resetPassword(userResetPasswordDTO));
    }

    @Operation(summary = "获取详情")
    @SaCheckPermission(PermissionConstant.USER_LIST)
    @PostMapping("/detail")
    public R<UserDetailVO> detail(@RequestBody @Valid IdDTO idDTO) {
        return R.success(userService.detail(idDTO));
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/current-user-info")
    public R<UserInfoVO> currentUserInfo() {
        return R.success(userService.currentUserInfo());
    }

    @Operation(summary = "用户下拉框")
    @GetMapping("/user-combobox")
    public R<List<SelectCombobox>> userCombobox(@RequestParam(value = "status", required = false)
                                                @Parameter(name = "status", description = "状态,不传默认所有(ENABLED:启用,DISABLE:禁用)")
                                                String status) {
        return R.success(userService.userCombobox(status));
    }

}

package com.wd.system.basic.controller;

import com.wd.common.core.model.Id;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.model.R;
import com.wd.system.basic.domain.dto.UserAddDTO;
import com.wd.system.basic.domain.dto.UserPageParamDTO;
import com.wd.system.basic.domain.dto.UserUpdateDTO;
import com.wd.system.basic.domain.vo.UserPageVO;
import com.wd.system.basic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Operation(summary = "列表查询")
    @PostMapping("/page")
    public R<PageInfo<UserPageVO>> pageList(@RequestBody UserPageParamDTO pageParam) {
        return R.success(userService.pageList(pageParam));
    }

    @Operation(summary = "新增用户")
    @PostMapping("/add")
    public R<Long> addUser(@RequestBody @Valid UserAddDTO userAddParam) {
        return R.success(userService.addUser(userAddParam));
    }

    @Operation(summary = "更新用户")
    @PutMapping("/update")
    public R<Boolean> updateUser(@RequestBody @Valid UserUpdateDTO userUpdateParam) {
        return R.success(userService.updateUser(userUpdateParam));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/delete")
    public R<Boolean> deleteUserById(@RequestBody @Valid Id id) {
        return R.success(userService.deleteUserById(id));
    }

}

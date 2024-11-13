package com.wd.system.controller.system;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.wd.common.core.PageInfo;
import com.wd.common.core.R;
import com.wd.common.core.SelectCombobox;
import com.wd.common.core.util.CodeDescEnumUtil;
import com.wd.system.system.dto.UserAddDTO;
import com.wd.system.system.dto.UserPageParam;
import com.wd.system.system.dto.UserUpdateDTO;
import com.wd.system.system.enums.UserSexEnum;
import com.wd.system.system.enums.UserStatusEnum;
import com.wd.system.system.enums.UserTypeEnum;
import com.wd.system.system.service.IUserService;
import com.wd.system.system.vo.UserPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author huangwenda
 * @since 2023-01-30
 */
@Tag(name = "用户管理")
@ApiSupport(order = 10)
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Operation(summary = "列表查询")
    @ApiOperationSupport(order = 10)
    @PostMapping("/page")
    public R<PageInfo<UserPageVO>> page(@RequestBody UserPageParam pageParam) {
        return R.success(userService.page(pageParam));
    }

    @Operation(summary = "列表导出")
    @ApiOperationSupport(order = 20)
    @PostMapping("/export-page")
    public R<Long> exportPage(@RequestBody UserPageParam pageParam) {
        return R.success(userService.exportPage(pageParam));
    }

    @Operation(summary = "导入")
    @ApiOperationSupport(order = 30)
    @PostMapping("/import-excel")
    public R<Boolean> importExcel(MultipartFile file) {
        return R.success(true);
    }

    @Operation(summary = "新增用户")
    @ApiOperationSupport(order = 40)
    @PostMapping
    public R<Boolean> add(@Valid @RequestBody UserAddDTO addUser) {
        return R.success(userService.add(addUser));
    }

    @Operation(summary = "更新用户")
    @ApiOperationSupport(order = 50)
    @PutMapping("/{id}")
    public R<Boolean> update(@PathVariable("id") Long id, @Valid @RequestBody UserUpdateDTO updateUser) {
        return R.success(userService.update(id, updateUser));
    }

    @Operation(summary = "删除用户")
    @ApiOperationSupport(order = 60)
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {
        return R.success(userService.deleteById(id));
    }

    @Operation(summary = "状态下拉框选项")
    @ApiOperationSupport(order = 2010)
    @GetMapping("/status-combobox")
    public R<List<SelectCombobox>> statusCombobox() {
        return R.success(CodeDescEnumUtil.convertSelectCombobox(UserStatusEnum.class));
    }

    @Operation(summary = "类型下拉框选项")
    @ApiOperationSupport(order = 2020)
    @GetMapping("/type-combobox")
    public R<List<SelectCombobox>> typeCombobox() {
        return R.success(CodeDescEnumUtil.convertSelectCombobox(UserTypeEnum.class, Set.of(UserTypeEnum.SYSTEM_SUPER_ADMIN, UserTypeEnum.SYSTEM_MANAGER)));
    }

    @Operation(summary = "性别下拉框选项")
    @ApiOperationSupport(order = 2030)
    @GetMapping("/sex-combobox")
    public R<List<SelectCombobox>> sexCombobox() {
        return R.success(CodeDescEnumUtil.convertSelectCombobox(UserSexEnum.class));
    }
}

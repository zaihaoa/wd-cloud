package com.wd.system.basic.controller;

import com.wd.common.core.model.R;
import com.wd.system.basic.domain.dto.UserConfigAddUpdateDTO;
import com.wd.system.basic.domain.dto.UserConfigQueryDTO;
import com.wd.system.basic.domain.vo.UserConfigDetailVO;
import com.wd.system.basic.service.UserConfigService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户配置 前端控制器
 * </p>
 *
 * @author huangwenda
 * @since 2026-11-09
 */
@RestController
@RequestMapping("/user-config")
public class UserConfigController {

    @Resource
    private UserConfigService userConfigService;

    @Operation(summary = "新增或更新")
    @PostMapping("/add-or-update")
    public R<UserConfigDetailVO> addOrUpdate(@RequestBody @Valid UserConfigAddUpdateDTO addUpdateDTO) {
        return R.success(userConfigService.addOrUpdate(addUpdateDTO));
    }

    @Operation(summary = "获取详情")
    @PostMapping("/detail")
    public R<UserConfigDetailVO> detail(@RequestBody @Valid UserConfigQueryDTO query) {
        return R.success(userConfigService.detail(query));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/delete")
    public R<Boolean> delete(@RequestBody @Valid UserConfigQueryDTO query) {
        return R.success(userConfigService.delete(query));
    }

}

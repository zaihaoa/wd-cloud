package com.wd.system.basic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.wd.common.core.annotions.ApifoxFolder;
import com.wd.common.core.constants.PermissionConstant;
import com.wd.common.core.model.IdDTO;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.model.R;
import com.wd.system.basic.domain.dto.GlobalConfigPageParamDTO;
import com.wd.system.basic.domain.vo.GlobalConfigPageVO;
import com.wd.system.basic.domain.vo.GlobalConfigDetailVO;
import com.wd.system.basic.domain.dto.GlobalConfigAddDTO;
import com.wd.system.basic.domain.dto.GlobalConfigUpdateDTO;
import com.wd.system.basic.service.GlobalConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 全局配置 前端控制器
 * </p>
 *
 * @author huangwenda
 * @since 2025-10-11
 */
@ApifoxFolder("全局配置")
@Tag(name = "全局配置")
@RestController
@RequestMapping("/global-config")
public class GlobalConfigController {

    @Resource
    private GlobalConfigService globalConfigService;

    @Operation(summary = "列表查询")
    @SaCheckPermission(PermissionConstant.GLOBAL_CONFIG_LIST)
    @PostMapping("/page")
    public R<PageInfo<GlobalConfigPageVO>> pageList(@RequestBody GlobalConfigPageParamDTO pageParam) {
        return R.success(globalConfigService.pageList(pageParam));
    }

    @Operation(summary = "新增全局配置")
    @SaCheckPermission(PermissionConstant.GLOBAL_CONFIG_ADD)
    @PostMapping("/add")
    public R<Long> addGlobalConfig(@RequestBody @Valid GlobalConfigAddDTO globalConfigAddParam) {
        return R.success(globalConfigService.addGlobalConfig(globalConfigAddParam));
    }

    @Operation(summary = "更新全局配置")
    @SaCheckPermission(PermissionConstant.GLOBAL_CONFIG_UPDATE)
    @PostMapping("/update")
    public R<Boolean> updateGlobalConfig(@RequestBody @Valid GlobalConfigUpdateDTO globalConfigUpdateParam) {
        return R.success(globalConfigService.updateGlobalConfig(globalConfigUpdateParam));
    }

    @Operation(summary = "获取全局配置详情")
    @SaCheckPermission(PermissionConstant.GLOBAL_CONFIG_LIST)
    @GetMapping("/detail")
    public R<GlobalConfigDetailVO> detail(@RequestParam("id") Long id) {
        return R.success(globalConfigService.detail(id));
    }

    @Operation(summary = "删除全局配置")
    @SaCheckPermission(PermissionConstant.GLOBAL_CONFIG_DELETE)
    @PostMapping("/delete")
    public R<Boolean> deleteGlobalConfigById(@RequestBody @Valid IdDTO id) {
        return R.success(globalConfigService.deleteGlobalConfigById(id));
    }

}

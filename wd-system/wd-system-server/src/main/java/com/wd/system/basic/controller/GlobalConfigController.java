package com.wd.system.basic.controller;

import com.wd.common.core.annotions.Folder;
import com.wd.common.core.model.Id;
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
@Folder("全局配置")
@Tag(name = "全局配置")
@RestController
@RequestMapping("/global-config")
public class GlobalConfigController {

    @Resource
    private GlobalConfigService globalConfigService;

    @Operation(summary = "列表查询")
    @PostMapping("/page")
    public R<PageInfo<GlobalConfigPageVO>> pageList(@RequestBody GlobalConfigPageParamDTO pageParam) {
        return R.success(globalConfigService.pageList(pageParam));
    }

    @Operation(summary = "新增全局配置")
    @PostMapping("/add")
    public R<Long> addGlobalConfig(@RequestBody @Valid GlobalConfigAddDTO globalConfigAddParam) {
        return R.success(globalConfigService.addGlobalConfig(globalConfigAddParam));
    }

    @Operation(summary = "更新全局配置")
    @PutMapping("/update")
    public R<Boolean> updateGlobalConfig(@RequestBody @Valid GlobalConfigUpdateDTO globalConfigUpdateParam) {
        return R.success(globalConfigService.updateGlobalConfig(globalConfigUpdateParam));
    }

    @Operation(summary = "获取全局配置详情")
    @GetMapping("/detail")
    public R<GlobalConfigDetailVO> detail(@RequestParam("id") Long id) {
        return R.success(globalConfigService.detail(id));
    }

    @Operation(summary = "删除全局配置")
    @DeleteMapping("/delete")
    public R<Boolean> deleteGlobalConfigById(@RequestBody @Valid Id id) {
        return R.success(globalConfigService.deleteGlobalConfigById(id));
    }

}

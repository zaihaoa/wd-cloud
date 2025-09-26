package com.wd.system.basic.controller.feign;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.wd.common.core.annotions.Folder;
import com.wd.common.core.model.R;
import com.wd.system.basic.domain.dto.CommonLogAddDTO;
import com.wd.system.basic.service.CommonLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 通用日志 前端控制器
 * </p>
 *
 * @author huangwenda
 * @since 2025-05-28
 */
@Slf4j
@Folder("通用日志/feign接口")
@Tag(name = "通用日志-feign接口")
@ApiSupport(order = 300)
@RestController
@RequestMapping("/feign/common-log")
public class CommonLogFeignController {

    @Resource
    private CommonLogService commonLogService;

    @Operation(summary = "新增日志")
    @ApiOperationSupport(order = 300)
    @PostMapping("/add-log")
    public R<Boolean> addLog(@RequestBody CommonLogAddDTO commonLogAddParam) {
        return R.success(commonLogService.addLog(commonLogAddParam));
    }

    @Operation(summary = "批量新增日志")
    @ApiOperationSupport(order = 300)
    @PostMapping("/batch-add-log")
    public R<Boolean> batchAddLog(@RequestBody List<CommonLogAddDTO> commonLogAddParams) {
        return R.success(commonLogService.batchAddLog(commonLogAddParams));
    }

}

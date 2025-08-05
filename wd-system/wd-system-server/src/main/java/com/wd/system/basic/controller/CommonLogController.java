package com.wd.system.basic.controller;

import com.wd.common.core.annotions.Folder;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.model.R;
import com.wd.system.basic.domain.dto.CommonLogPageParamDTO;
import com.wd.system.basic.domain.vo.CommonLogPageVO;
import com.wd.system.basic.service.CommonLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 通用日志 前端控制器
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Folder("通用日志")
@Tag(name = "通用日志")
@RestController
@RequestMapping("/common-log")
public class CommonLogController {

    @Resource
    private CommonLogService commonLogService;

    @Operation(summary = "列表查询")
    @PostMapping("/page")
    public R<PageInfo<CommonLogPageVO>> pageList(@RequestBody CommonLogPageParamDTO pageParam) {
        return R.success(commonLogService.pageList(pageParam));
    }

}

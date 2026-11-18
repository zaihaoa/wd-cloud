package com.wd.system.basic.controller.feign;

import com.wd.common.core.annotions.ApifoxFolder;
import com.wd.common.core.model.R;
import com.wd.system.basic.service.GlobalConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@ApifoxFolder("全局配置/feign接口")
@Tag(name = "全局配置-feign接口")
@RestController
@RequestMapping("/feign/global-config")
public class GlobalConfigFeignController {

    @Resource
    private GlobalConfigService globalConfigService;

    @Operation(summary = "按编码获取全局配置值")
    @GetMapping("/get")
    public R<String> getValueByCode(@RequestParam("code") String code) {
        return R.success(globalConfigService.getValueByCode(code));
    }
}

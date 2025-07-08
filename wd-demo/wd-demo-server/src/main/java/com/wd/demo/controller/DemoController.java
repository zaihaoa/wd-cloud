package com.wd.demo.controller;

import com.wd.common.core.model.R;
import com.wd.system.system.feign.GlobalConfigFeign;
import com.wd.system.system.feign.UserFeign;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author huangwenda
 * @date 2023/3/28 14:29
 **/
@Slf4j
@Tag(name = "演示")
@RestController
@RequestMapping("/demo")
public class DemoController {

    private UserFeign userApi;

    private GlobalConfigFeign globalConfigApi;

    @Operation(summary = "根据用户ID获取用户显示名称")
    @GetMapping("/show-name")
    public R<String> getShowName(Long userId) {
        log.info("用户ID:{}", userId);
        R<String> R = userApi.getShowName(userId);
        return R.result(R.getCode(), R.getData(), R.getMessage());
    }

    @Operation(summary = "获取用户显示名称列表")
    @GetMapping("/show-name-map")
    public R<Map<Long, String>> getShowNameMap() {
        R<Map<Long, String>> R = userApi.getShowNameMap(null);
        return R.result(R.getCode(), R.getData(), R.getMessage());
    }

    @Operation(summary = "根据配置code获取value")
    @GetMapping("/config-value")
    public R<String> getValueByCode(String code) {
        R<String> R = globalConfigApi.getValueByCode(code);
        return R.result(R.getCode(), R.getData(), R.getMessage());
    }
}

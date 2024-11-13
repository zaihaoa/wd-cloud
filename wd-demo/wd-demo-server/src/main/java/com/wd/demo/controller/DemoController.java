package com.wd.demo.controller;

import com.wd.common.core.R;
import com.wd.common.core.RpcResponse;
import com.wd.system.system.api.GlobalConfigApi;
import com.wd.system.system.api.UserApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
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

    @DubboReference
    private UserApi userApi;

    @DubboReference
    private GlobalConfigApi globalConfigApi;

    @Operation(summary = "根据用户ID获取用户显示名称")
    @GetMapping("/show-name")
    public R<String> getShowName(Long userId) {
        log.info("用户ID:{}", userId);
        RpcResponse<String> rpcResponse = userApi.getShowName(userId);
        return R.result(rpcResponse.getCode(), rpcResponse.getData(), rpcResponse.getMessage());
    }

    @Operation(summary = "获取用户显示名称列表")
    @GetMapping("/show-name-map")
    public R<Map<Long, String>> getShowNameMap() {
        RpcResponse<Map<Long, String>> rpcResponse = userApi.getShowNameMap(null);
        return R.result(rpcResponse.getCode(), rpcResponse.getData(), rpcResponse.getMessage());
    }

    @Operation(summary = "根据配置code获取value")
    @GetMapping("/config-value")
    public R<String> getValueByCode(String code) {
        RpcResponse<String> rpcResponse = globalConfigApi.getValueByCode(code);
        return R.result(rpcResponse.getCode(), rpcResponse.getData(), rpcResponse.getMessage());
    }
}

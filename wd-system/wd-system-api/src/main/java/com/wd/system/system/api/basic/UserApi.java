package com.wd.system.system.api.basic;

import com.wd.common.core.model.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Map;

/**
 * @author huangwenda
 * @date 2023/2/27 16:48
 **/
@FeignClient(
        name = "system-server",
        contextId = "userApi",
        path = "/feign/user")
public interface UserApi {

    @GetMapping("/show-name")
    R<String> getShowName(@RequestParam("userId") Long userId);

    @GetMapping("/show-name-map")
    R<Map<Long, String>> getShowNameMap(@RequestParam("userIds") Collection<Long> userIds);
}

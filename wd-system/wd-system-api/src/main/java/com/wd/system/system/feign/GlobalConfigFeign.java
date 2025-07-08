package com.wd.system.system.feign;

import com.wd.common.core.model.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author huangwenda
 * @date 2023/2/27 16:48
 **/
@FeignClient(name = "globalConfigFeign")
public interface GlobalConfigFeign {

    /**
     * 根据code获取value值
     *
     * @param code code
     * @return value值
     */
    @GetMapping("/get")
    R<String> getValueByCode(@RequestParam("code") String code);
}

package com.wd.system.system.api;

import com.wd.common.core.RpcResponse;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author huangwenda
 * @date 2023/3/28 14:24
 **/
@DubboService
public class GlobalConfigApiImpl implements GlobalConfigApi {
    @Override
    public RpcResponse<String> getValueByCode(String code) {
        return RpcResponse.success("test");
    }
}

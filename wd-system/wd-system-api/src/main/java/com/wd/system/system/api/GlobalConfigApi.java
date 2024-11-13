package com.wd.system.system.api;

import com.wd.common.core.RpcResponse;

/**
 * @author huangwenda
 * @date 2023/2/27 16:48
 **/
public interface GlobalConfigApi {

    /**
     * 根据code获取value值
     *
     * @param code code
     * @return value值
     */
    RpcResponse<String> getValueByCode(String code);
}

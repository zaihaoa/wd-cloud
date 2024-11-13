package com.wd.system.system.api;

import com.wd.common.core.R;
import com.wd.common.core.RpcResponse;

import java.util.Collection;
import java.util.Map;

/**
 * @author huangwenda
 * @date 2023/2/27 16:48
 **/
public interface UserApi {

    RpcResponse<String> getShowName(Long userId);

    RpcResponse<Map<Long, String>> getShowNameMap(Collection<Long> userIds);
}

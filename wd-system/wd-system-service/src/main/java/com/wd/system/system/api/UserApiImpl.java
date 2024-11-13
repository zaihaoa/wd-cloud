package com.wd.system.system.api;

import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.RpcResponse;
import com.wd.system.system.entity.User;
import com.wd.system.system.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author huangwenda
 * @date 2023/2/27 16:54
 **/
@Slf4j
@DubboService
@Service
public class UserApiImpl implements UserApi {

    @Autowired
    private IUserService userService;

    @Override
    public RpcResponse<String> getShowName(Long userId) {
        log.debug("用户ID:{}", userId);
        log.info("用户ID:{}", userId);
        log.warn("用户ID:{}", userId);
        log.error("用户ID:{}", userId);
        if (userId == null) {
            return RpcResponse.success();
        }
        if (CommonConstant.SYSTEM_USER_ID.equals(userId)) {
            return RpcResponse.success(CommonConstant.SYSTEM_USER_REAL_NAME);
        }
        return RpcResponse.success(Optional
                .ofNullable(userService.getById(userId))
                .map(User::getRealName)
                .orElse(null));
    }

    @Override
    public RpcResponse<Map<Long, String>> getShowNameMap(Collection<Long> userIds) {
        Map<Long, String> map = new HashMap<>();

        map.put(CommonConstant.SYSTEM_USER_ID, CommonConstant.SYSTEM_USER_REAL_NAME);
        return RpcResponse.success(map);
    }
}

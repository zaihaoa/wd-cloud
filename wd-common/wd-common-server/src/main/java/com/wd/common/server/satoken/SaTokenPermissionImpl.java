package com.wd.common.server.satoken;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.wd.common.core.util.ResponseUtil;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.RedisKey;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.system.api.basic.PermissionApi;
import com.wd.system.system.api.basic.dto.UserPermissionDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Objects;

/**
 *
 *
 * @author huangwenda
 * @date 2024/4/29 21:12
 **/
@Component
public class SaTokenPermissionImpl implements StpInterface {

    @Resource
    private RedisHelper redisHelper;
    @Resource
    private PermissionApi permissionApi;


    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {

        // 用户id
        long userId = Long.parseLong(String.valueOf(loginId));

        // 缓存获取,则调用feign接口获取
        String key = RedisHelper.joinKey(RedisKey.USER_PERMISSION, userId);

        UserPermissionDTO userPermission = redisHelper.get(CacheRegion.PERMISSION, key, new TypeReference<>() {
        });

        if (Objects.isNull(userPermission)) {
            // 调用接口获取权限
            userPermission = ResponseUtil.assertSuccessGetData(permissionApi.getUserPermission(userId));
        }

        if (Objects.isNull(userPermission)) {
            // 没有权限
            return Lists.newArrayList();
        }

        if (Boolean.TRUE.equals(userPermission.getAdminUser()) || Boolean.TRUE.equals(userPermission.getAdminRole())) {
            return List.of("*");
        }

        List<String> permissionList = Lists.newArrayList();
        if (CollUtil.isNotEmpty(userPermission.getAuthMenuPermissionList())) {
            permissionList.addAll(userPermission.getAuthMenuPermissionList());
        }
        if (CollUtil.isNotEmpty(userPermission.getAuthFieldPermissionList())) {
            permissionList.addAll(userPermission.getAuthFieldPermissionList());
        }
        return permissionList;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return List.of();
    }
}

package com.kvc.common.satoken;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 *
 * @author huangwenda
 * @date 2024/4/29 21:12
 **/
@Component
public class SaTokenPermissionImpl implements StpInterface {

//    @Autowired
//    private RedisHelper redisHelper;
//    @Autowired
//    private PermissionApi permissionApi;


    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {

//        // 用户id
//        long userId = Long.parseLong(String.valueOf(loginId));
//
//        // 缓存获取,则调用feign接口获取
//        String key = RedisHelper.joinKey(RedisKey.USER_PERMISSION, userId);
//
//        UserPermissionDTO cache = redisHelper.get(CacheRegion.PERMISSION, key, new TypeReference<>() {
//        });
//
//        if (Objects.nonNull(cache)) {
//            if (cache.getAdminRole()) {
//                return List.of("*");
//            }
//            return cache.getAuthMenuPermissionList();
//        }
//
//        // 用户权限
//        UserPermissionDTO userPermission = ResponseUtil.assertSuccessGetData(permissionApi.getUserPermission(userId));
//        if (userPermission.getAdminRole()) {
//            return List.of("*");
//        }
//
//        return userPermission.getAuthMenuPermissionList();

        return List.of("*");
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return List.of();
    }
}

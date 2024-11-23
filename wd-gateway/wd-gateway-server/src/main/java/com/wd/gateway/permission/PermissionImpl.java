package com.wd.gateway.permission;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.gerp.celebrity.common.core.constants.CommonConstant;
import com.gerp.celebrity.common.core.util.ResponseUtil;
import com.gerp.celebrity.system.feign.UserFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 *
 *
 * @author huangwenda
 * @date 2024/4/29 21:12
 **/
@Component
public class PermissionImpl implements StpInterface {

//    /**
//     * 缓存权限
//     */
//    private static final Cache<String, List<String>> cache;
//
//    /**
//     * 初始化Caffeine缓存配置
//     */
//    static {
//        cache = Caffeine.newBuilder()
//                .maximumSize(10000)
//                .expireAfterWrite(5, TimeUnit.MINUTES)
//                .build();
//    }

    @Autowired
    private UserFeign userFeign;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {

        // 公司ID
        String companyIdStr = (String) StpUtil.getExtra(CommonConstant.COMPANY_ID);
        long companyId = Long.parseLong(companyIdStr);

        // 用户id
        long userId = Long.parseLong(String.valueOf(loginId));

        // 缓存key
//        String cacheKey = String.format("%s_%s", companyId, userId);

        // 先从内存缓存获取
//        List<String> cachePermissionList = cache.getIfPresent(cacheKey);
//        if (Objects.nonNull(cachePermissionList)) {
//            return cachePermissionList;
//        }

        // 内存缓存不存在,则调用feign接口获取
        List<String> permissionList = ResponseUtil.assertSuccessGetData(userFeign.getUserPermissionList(companyId, userId));

        // 为null表示所有权限
        if (Objects.isNull(permissionList)) {
            return List.of("*");
//            List<String> actualPermissionList = List.of("*");
//            cache.put(cacheKey, actualPermissionList);
//            return actualPermissionList;
        }

        // 对应的权限集合
//        cache.put(cacheKey, permissionList);
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

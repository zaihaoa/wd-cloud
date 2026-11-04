package com.wd.common.server.config;

import cn.dev33.satoken.stp.StpUtil;
import com.wd.common.core.model.FieldPermissionChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.annotation.RequestScope;

import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @author huangwenda
 * @date 2026/11/4 15:23
 **/
@Configuration
public class FieldPermissionConfig {

    @Bean
    @RequestScope(proxyMode = ScopedProxyMode.INTERFACES)
    public FieldPermissionChecker fieldPermissionChecker() {
        Set<String> permissions = StpUtil.isLogin()
                ? new HashSet<>(StpUtil.getPermissionList())
                : Set.of();

        return code -> permissions.contains("*") || permissions.contains(code);
    }
}

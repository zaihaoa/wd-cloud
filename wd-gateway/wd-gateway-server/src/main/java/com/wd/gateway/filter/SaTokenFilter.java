package com.wd.gateway.filter;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.gerp.celebrity.common.core.R;
import com.gerp.celebrity.common.core.constants.CommonConstant;
import com.gerp.celebrity.common.core.context.CommonParam;
import com.gerp.celebrity.common.core.context.SystemContext;
import com.gerp.celebrity.common.core.enums.ResponseCodeEnum;
import com.gerp.celebrity.gateway.properties.RouteProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 *
 * @author huangwenda
 * @date 2024/4/29 20:33
 **/
@Slf4j
@Configuration
public class SaTokenFilter {

    // 注册 Sa-Token全局过滤器
    @Bean
    public SaReactorFilter getSaReactorFilter(RouteProperties routeProperties) {
        return new SaReactorFilter()

                // 拦截地址
                .addInclude("/**")

                // 前置函数：在每次认证函数之前执行（BeforeAuth 不受 includeList 与 excludeList 的限制，所有请求都会进入）
                .setBeforeAuth(obj -> {

                    // 通用参数
                    CommonParam commonParam = new CommonParam();
                    // 系统上下文
                    SystemContext.set(commonParam);

                    SaRequest request = SaHolder.getRequest();
                    SaResponse response = SaHolder.getResponse();

                    // traceId
                    String traceId = get32UUID();
                    commonParam.setTraceId(traceId);
                    MDC.put(CommonConstant.TRACE_ID, traceId);
                    // 设置到响应头
                    response.setHeader(CommonConstant.HEADER_TRACE_ID, traceId);

                    if (StpUtil.isLogin()) {
                        // 登录

                        // 用户id
                        long userId = StpUtil.getLoginIdAsLong();
                        commonParam.setUserId(userId);
                        MDC.put(CommonConstant.USER_ID, String.valueOf(userId));

                        // 公司ID
                        String companyIdStr = (String) StpUtil.getExtra(CommonConstant.COMPANY_ID);
                        long companyId = Long.parseLong(companyIdStr);
                        commonParam.setCompanyId(companyId);
                        MDC.put(CommonConstant.COMPANY_ID, String.valueOf(companyId));

                    } else {
                        // 未登录

                        // 游客id
                        String visitorId = request.getHeader(CommonConstant.HEADER_VISITOR_ID);
                        if (!StringUtils.hasText(visitorId)) {
                            visitorId = "s_" + get32UUID().substring(0, 10);
                            commonParam.setVisitorId(visitorId);
                            MDC.put(CommonConstant.VISITOR_ID, visitorId);
                        }
                    }
                })

                // 鉴权方法：每次访问进入
                .setAuth(obj -> {

                    // 校验是否登录
                    SaRouter.match("/**")
                            .notMatch(routeProperties.getActualExcludeList())
                            .check(r -> StpUtil.checkLogin());

                    // 需要权限的路径
                    List<RouteProperties.PermissionPath> permissionPaths = routeProperties.getPermissionPaths();
                    if (!CollectionUtils.isEmpty(permissionPaths)) {
                        for (RouteProperties.PermissionPath permissionPath : permissionPaths) {

                            // 路径
                            SaRouterStaff routerStaff = SaRouter.match(permissionPath.getPath());

                            // 请求方式
                            String method = permissionPath.getMethod();
                            if (StringUtils.hasText(method)) {
                                routerStaff.matchMethod(method);
                            }

                            // 权限认证
                            routerStaff.check(r -> StpUtil.checkPermission(permissionPath.getPermission())).stop();
                        }
                    }
                })

                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> {
                    // 未登录
                    if (e instanceof NotLoginException) {
                        SaHolder.getResponse().setStatus(HttpStatus.UNAUTHORIZED.value());
                        return JSON.toJSONString(R.failure(ResponseCodeEnum.NOT_LOGIN.getCode(), ResponseCodeEnum.NOT_LOGIN.getPrompt()));
                    }

                    // 没权限
                    if (e instanceof NotPermissionException) {
                        SaHolder.getResponse().setStatus(HttpStatus.FORBIDDEN.value());
                        return JSON.toJSONString(R.failure(ResponseCodeEnum.NOT_PERMISSION.getCode(), ResponseCodeEnum.NOT_PERMISSION.getPrompt()));
                    }
                    return JSON.toJSONString(R.failure(e.getMessage()));
                });
    }

    private static String get32UUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return (new UUID(random.nextLong(), random.nextLong())).toString().replace("-", "");
    }
}

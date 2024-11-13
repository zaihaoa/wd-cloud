package com.wd.common.web.interceptor;

import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.context.CommonParam;
import com.wd.common.core.context.SystemContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;
import java.util.UUID;

/**
 * @author huangwenda
 * @date 2023/3/29 14:05
 **/
@Component
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 设置
        setSystemContext(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }


    private void setSystemContext(HttpServletRequest request) {
        CommonParam context = SystemContext.get();
        // traceId
        String traceId = Optional.ofNullable(request.getHeader(CommonConstant.HEADER_TRACE_ID))
                .orElseGet(() -> UUID.randomUUID().toString().replace("-", ""));
        context.setTraceId(traceId);
        MDC.put(CommonConstant.TRACE_ID, traceId);
        // 用户ID
        Optional.ofNullable(request.getHeader(CommonConstant.HEADER_USER_ID))
                .ifPresent(userId -> {
                    context.setUserId(Long.valueOf(userId));
                    MDC.put(CommonConstant.USER_ID, userId);
                });
        // 登录名
        Optional.ofNullable(request.getHeader(CommonConstant.HEADER_USER_LOGIN_NAME))
                .ifPresent(userLoginName -> {
                    context.setUserLoginName(userLoginName);
                    MDC.put(CommonConstant.USER_LOGIN_NAME, userLoginName);
                });
        // 真实姓名
        Optional.ofNullable(request.getHeader(CommonConstant.HEADER_USER_REAL_NAME))
                .ifPresent(userRealName -> {
                    context.setUserRealName(userRealName);
                    MDC.put(CommonConstant.USER_REAL_NAME, userRealName);
                });
    }
}

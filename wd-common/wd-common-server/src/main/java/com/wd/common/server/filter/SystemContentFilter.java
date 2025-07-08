package com.wd.common.server.filter;

import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.context.CommonParam;
import com.wd.common.core.context.SystemContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *
 *
 * @author huangwenda
 * @date 2024/4/29 16:50
 **/
@Slf4j
@Component
@Order(-50)
public class SystemContentFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        // 设置系统上下文
        setCommonParam(request);

        try {
            // 往下执行
            chain.doFilter(req, res);
        } finally {
            // 删除线程上下文
            SystemContext.set(null);
            // 删除MDC
            MDC.clear();
        }
    }

    private void setCommonParam(HttpServletRequest request) {
        // 通用参数
        CommonParam commonParam = new CommonParam();

        // 用户id
        String userId = request.getHeader(CommonConstant.HEADER_USER_ID);
        if (StringUtils.isNotBlank(userId)) {
            commonParam.setUserId(Long.valueOf(userId));
            MDC.put(CommonConstant.USER_ID, userId);
        }

        // traceId
        String traceId = request.getHeader(CommonConstant.HEADER_TRACE_ID);
        if (StringUtils.isNotBlank(traceId)) {
            commonParam.setTraceId(traceId);
            MDC.put(CommonConstant.TRACE_ID, traceId);
        }

        // traceId
        String traceExtra = request.getHeader(CommonConstant.HEADER_TRACE_EXTRA);
        if (StringUtils.isNotBlank(traceExtra)) {
            commonParam.setTraceExtra(traceExtra);
            MDC.put(CommonConstant.TRACE_EXTRA, traceExtra);
        }

        // 请求IP
        String requestIp = request.getHeader(CommonConstant.HEADER_REQUEST_IP);
        if (StringUtils.isNotBlank(requestIp)) {
            commonParam.setRequestIp(requestIp);
            MDC.put(CommonConstant.REQUEST_IP, requestIp);
        }

        // 设置线程数据
        SystemContext.set(commonParam);
    }
}

package com.wd.gateway.filter;

import com.gerp.celebrity.common.core.constants.CommonConstant;
import com.gerp.celebrity.common.core.context.CommonParam;
import com.gerp.celebrity.common.core.context.SystemContext;
import com.gerp.celebrity.gateway.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 *
 *
 * @author huangwenda
 * @date 2024/5/1 18:16
 **/
@Slf4j
@Component
@Order(-70)
public class RequestHeaderFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // ip
        String ip = RequestUtil.getIpAddress(request);
        MDC.put(CommonConstant.REQUEST_IP, ip);

        ServerHttpRequest newRequest = request
                .mutate()
                // 移除ce相关header,不接受header直接传参
                .headers(h -> h.remove(CommonConstant.HEADER_COMPANY_ID))
                .headers(h -> h.remove(CommonConstant.HEADER_USER_ID))
                .headers(h -> h.remove(CommonConstant.HEADER_VISITOR_ID))
                .headers(h -> h.remove(CommonConstant.HEADER_TRACE_ID))
                .headers(h -> h.remove(CommonConstant.HEADER_TRACE_EXTRA))
                .headers(h -> h.remove(CommonConstant.HEADER_REQUEST_IP))
                // 添加header
                .headers(headers -> {
                    CommonParam commonParam = SystemContext.get();

                    // 公司id
                    Long companyId = commonParam.getCompanyId();
                    if (Objects.nonNull(companyId)) {
                        headers.set(CommonConstant.HEADER_COMPANY_ID, String.valueOf(companyId));
                    }

                    // 用户id
                    Long userId = commonParam.getUserId();
                    if (Objects.nonNull(userId)) {
                        headers.set(CommonConstant.HEADER_USER_ID, String.valueOf(userId));
                    }

                    // 游客ID
                    String visitorId = commonParam.getVisitorId();
                    if (StringUtils.hasText(visitorId)) {
                        headers.set(CommonConstant.HEADER_VISITOR_ID, visitorId);
                    }

                    // traceId
                    String traceId = commonParam.getTraceId();
                    if (StringUtils.hasText(traceId)) {
                        headers.set(CommonConstant.HEADER_TRACE_ID, traceId);
                    }

                    // traceExtra
                    String traceExtra = commonParam.getTraceExtra();
                    if (StringUtils.hasText(traceExtra)) {
                        headers.set(CommonConstant.HEADER_TRACE_EXTRA, traceExtra);
                    }

                    // 请求ip
                    headers.set(CommonConstant.HEADER_REQUEST_IP, ip);
                })
                .build();

        return chain.filter(exchange.mutate().request(newRequest).build());
    }
}

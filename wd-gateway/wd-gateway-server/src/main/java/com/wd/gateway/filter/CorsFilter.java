package com.wd.gateway.filter;

import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.context.SystemContext;
import com.wd.gateway.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 *
 * @author huangwenda
 * @date 2024/4/29 15:44
 **/
@Slf4j
@Component
@Order(-10000)
public class CorsFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 响应头
        HttpHeaders responseHeaders = response.getHeaders();

        responseHeaders.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        // 允许指定域访问跨域资源
        responseHeaders.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        // 允许所有请求方式
        responseHeaders.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "PUT, GET, POST, DELETE, OPTIONS");
        // 允许的header参数
        responseHeaders.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        // 有效时间
        responseHeaders.set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");

        responseHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");

        if (request.getMethod() == HttpMethod.OPTIONS) {
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }

        long start = System.currentTimeMillis();

        // 路径
        String path = RequestUtil.getPathTemplate(request);

        // traceId
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String traceId = new UUID(random.nextLong(), random.nextLong()).toString().replace("-", "");
        MDC.put(CommonConstant.TRACE_ID, traceId);

        // 请求ip
        String requestIp = RequestUtil.getIpAddress(request);
        MDC.put(CommonConstant.REQUEST_IP, requestIp);

        // 设置到响应头
        responseHeaders.set(CommonConstant.HEADER_TRACE_ID, traceId);
        responseHeaders.set(CommonConstant.HEADER_REQUEST_IP, requestIp);

        return chain.filter(exchange)
                .doFinally(r -> {

                    HttpHeaders finalResponseHeaders = response.getHeaders();

                    MDC.put(CommonConstant.TRACE_ID, finalResponseHeaders.getFirst(CommonConstant.HEADER_TRACE_ID));
                    MDC.put(CommonConstant.REQUEST_IP, finalResponseHeaders.getFirst(CommonConstant.HEADER_REQUEST_IP));
                    MDC.put(CommonConstant.USER_ID, finalResponseHeaders.getFirst(CommonConstant.HEADER_USER_ID));

                    log.info("path:{},time:{}", path, System.currentTimeMillis() - start);

                    // 清除上下文
                    SystemContext.set(null);
                    MDC.clear();
                });
    }
}

package com.wd.gateway.filter;

import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.context.SystemContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
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
public class RequestHeaderFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        HttpHeaders responseHeaders = response.getHeaders();

        ServerHttpRequest newRequest = request
                .mutate()
                // 移除ce相关header,不接受header直接传参
                .headers(h -> h.remove(CommonConstant.HEADER_TRACE_ID))
                .headers(h -> h.remove(CommonConstant.HEADER_TRACE_EXTRA))
                .headers(h -> h.remove(CommonConstant.HEADER_REQUEST_IP))
                .headers(h -> h.remove(CommonConstant.HEADER_USER_ID))
                // 添加header
                .headers(headers -> {

                    String traceId = responseHeaders.getFirst(CommonConstant.HEADER_TRACE_ID);
                    headers.set(CommonConstant.HEADER_TRACE_ID, traceId);

                    String requestIp = responseHeaders.getFirst(CommonConstant.HEADER_REQUEST_IP);
                    headers.set(CommonConstant.HEADER_REQUEST_IP, requestIp);


                    // 用户ID
                    Long userId = SystemContext.getUserId();
                    if (Objects.nonNull(userId)) {
                        headers.set(CommonConstant.HEADER_USER_ID, String.valueOf(userId));
                        responseHeaders.set(CommonConstant.HEADER_USER_ID, String.valueOf(userId));
                    }
                })
                .build();

        return chain.filter(exchange.mutate().request(newRequest).build());
    }
}

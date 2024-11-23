package com.wd.gateway.ratelimit;

import com.gerp.celebrity.gateway.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.HttpStatusHolder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.setResponseStatus;

/**
 *
 *
 * @author huangwenda
 * @date 2024/4/30 15:41
 **/
@Slf4j
@Component
public class GatewayRequestRateLimiterGatewayFilterFactory extends RequestRateLimiterGatewayFilterFactory {

    private boolean denyEmptyKey = false;

    private String emptyKeyStatusCode = HttpStatus.FORBIDDEN.name();

    private static final String EMPTY_KEY = "____EMPTY_KEY__";

    private final RateLimiter defaultRateLimiter;

    private final KeyResolver defaultKeyResolver;

    public GatewayRequestRateLimiterGatewayFilterFactory(RateLimiter defaultRateLimiter, KeyResolver defaultKeyResolver) {
        super(defaultRateLimiter, defaultKeyResolver);
        this.defaultRateLimiter = defaultRateLimiter;
        this.defaultKeyResolver = defaultKeyResolver;
    }

    @Override
    public GatewayFilter apply(Config config) {
        KeyResolver resolver = getOrDefault(config.getKeyResolver(), defaultKeyResolver);
        RateLimiter<Object> limiter = getOrDefault(config.getRateLimiter(), defaultRateLimiter);
        boolean denyEmpty = getOrDefault(config.getDenyEmptyKey(), this.denyEmptyKey);
        HttpStatusHolder emptyKeyStatus = HttpStatusHolder
                .parse(getOrDefault(config.getEmptyKeyStatus(), this.emptyKeyStatusCode));

        return (exchange, chain) -> resolver.resolve(exchange).defaultIfEmpty(EMPTY_KEY).flatMap(key -> {
            if (EMPTY_KEY.equals(key)) {
                if (denyEmpty) {
                    setResponseStatus(exchange, emptyKeyStatus);
                    return exchange.getResponse().setComplete();
                }
                return chain.filter(exchange);
            }
            String routeId = config.getRouteId();
            if (routeId == null) {
                Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                routeId = route.getId();
            }
            return limiter.isAllowed(routeId, key).flatMap(response -> {

//                for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
//                    exchange.getResponse().getHeaders().add(header.getKey(), header.getValue());
//                }

                if (response.isAllowed()) {
                    return chain.filter(exchange);
                }
                String path = RequestUtil.getPathTemplate(exchange.getRequest());
                log.info("已限流,path:{}", path);

//                setResponseStatus(exchange, config.getStatusCode());
//                return exchange.getResponse().setComplete();

                ServerHttpResponse httpResponse = exchange.getResponse();
                //修改code
                httpResponse.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                if (!httpResponse.getHeaders().containsKey("Content-Type")) {
                    httpResponse.getHeaders().add("Content-Type", "application/json");
                }
                //此处无法触发全局异常处理，手动返回
                DataBuffer buffer = httpResponse.bufferFactory().wrap(("""
                        {"code":429,"message":"请求过于频繁,请稍候再试"}
                        """).getBytes(StandardCharsets.UTF_8));
                return httpResponse.writeWith(Mono.just(buffer));
            });
        });
    }

    private <T> T getOrDefault(T configValue, T defaultValue) {
        return (configValue != null) ? configValue : defaultValue;
    }
}

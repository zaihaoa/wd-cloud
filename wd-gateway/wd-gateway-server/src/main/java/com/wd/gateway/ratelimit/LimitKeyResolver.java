package com.wd.gateway.ratelimit;

import cn.dev33.satoken.spring.pathmatch.SaPathPatternParserUtil;
import com.gerp.celebrity.common.core.constants.CommonConstant;
import com.gerp.celebrity.gateway.properties.RouteProperties;
import com.gerp.celebrity.gateway.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 *
 *
 * @author huangwenda
 * @date 2024/4/30 15:40
 **/
@Slf4j
@Component
public class LimitKeyResolver implements KeyResolver {

    @Autowired
    private RouteProperties routeProperties;

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        if (!isMatch(routeProperties.getActualRateLimitPaths(), request.getPath().value())) {
            // 不需要限流,返回空,直接放行
            return Mono.empty();
        }

        String path = RequestUtil.getPathTemplate(request);

        // token
        String token = request.getHeaders().getFirst(CommonConstant.HEADER_TOKEN);
        if (StringUtils.hasText(token)) {
            return Mono.just(String.format("%s_%s", path, token));
        }

        String ip = RequestUtil.getIpAddress(request);
        return Mono.just(String.format("%s_%s", path, ip));
    }

    public static boolean isMatch(List<String> patterns, String path) {
        if (patterns == null) {
            return false;
        }
        for (String pattern : patterns) {
            if (SaPathPatternParserUtil.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}

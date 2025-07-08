package com.wd.gateway.util;

import cn.hutool.core.util.NumberUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;

/**
 *
 *
 * @author huangwenda
 * @date 2024/5/2 18:53
 **/
public class RequestUtil {

    /**
     * 获取ip地址
     */
    public static String getIpAddress(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.contains(",")) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 获取请求路径
     * /op/star/base-info/107955 -> /op/star/base-info/{}
     * /op/star -> /op/star
     */
    public static String getPathTemplate(ServerHttpRequest request) {
        RequestPath requestPath = request.getPath();

        List<PathContainer.Element> elements = requestPath.elements();
        // 最后一个元素
        String lastValue = elements.get(elements.size() - 1).value();

        if (NumberUtil.isNumber(lastValue)) {
            // 替换路径变量
            return requestPath.value().replaceAll(lastValue, "");
        } else {
            return requestPath.value();
        }
    }
}

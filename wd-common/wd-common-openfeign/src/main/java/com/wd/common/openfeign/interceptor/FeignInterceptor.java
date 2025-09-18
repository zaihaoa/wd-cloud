package com.wd.common.openfeign.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.context.CommonParam;
import com.wd.common.core.context.SystemContext;
import com.wd.common.openfeign.properties.FeignMockProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 *
 *
 * @author huangwenda
 * @date 2024/4/29 16:32
 **/
@Slf4j
@Component
public class FeignInterceptor implements RequestInterceptor {

    @Autowired
    private FeignMockProperties feignMockProperties;

    @Override
    public void apply(RequestTemplate requestTemplate) {

        // 设置线程参数到请求头
        setCommonParamHeader(requestTemplate);

        // mock参数
        replaceMockUrl(requestTemplate);
    }

    private void setCommonParamHeader(RequestTemplate requestTemplate) {
        CommonParam commonParam = SystemContext.get();

        // 用户id
        Long userId = commonParam.getUserId();
        if (Objects.nonNull(userId)) {
            requestTemplate.header(CommonConstant.HEADER_USER_ID, String.valueOf(userId));
        }

        // traceId
        String traceId = commonParam.getTraceId();
        if (StringUtils.isBlank(traceId)) {
            traceId = MDC.get(CommonConstant.TRACE_ID);
        }
        if (StringUtils.isNotBlank(traceId)) {
            requestTemplate.header(CommonConstant.HEADER_TRACE_ID, traceId);
        }

        // traceExtra
        String traceExtra = commonParam.getTraceExtra();
        if (StringUtils.isBlank(traceExtra)) {
            traceExtra = MDC.get(CommonConstant.TRACE_EXTRA);
        }
        if (StringUtils.isNotBlank(traceExtra)) {
            requestTemplate.header(CommonConstant.HEADER_TRACE_EXTRA, traceExtra);
        }

        // 请求IP
        String requestIp = commonParam.getRequestIp();
        if (StringUtils.isNotBlank(requestIp)) {
            requestTemplate.header(CommonConstant.HEADER_REQUEST_IP, requestIp);
        }
    }

    private void replaceMockUrl(RequestTemplate requestTemplate) {
        // 没有开启mock
        if (!Optional.ofNullable(feignMockProperties).map(FeignMockProperties::getEnabled).orElse(false)) {
            return;
        }

        // map<请求服务名, 替换的url>
        Map<String, FeignMockProperties.AssignUrl> servers = feignMockProperties.getServers();
        if (Objects.isNull(servers)) {
            return;
        }

        // 服务名称
        String serviceName = requestTemplate.feignTarget().name();

        // 指定的url
        FeignMockProperties.AssignUrl assignUrl = servers.get(serviceName);
        if (Objects.isNull(assignUrl) || StringUtils.isBlank(assignUrl.getTargetUrl())) {
            log.info("不需要替换URL");
            return;
        }

        // 服务
        String host = assignUrl.getTargetUrl();

        // path前缀
        String prefixPath = StringUtils.substringAfter(requestTemplate.feignTarget().url(), serviceName);
        // 完整的请求url
        String fullPath = prefixPath + requestTemplate.path();

        // 包含的路径
        List<String> includePaths = assignUrl.getIncludePath();
        // 排除的路径
        List<String> excludePaths = assignUrl.getExcludePath();

        if ((CollectionUtil.isEmpty(includePaths) && CollectionUtil.isEmpty(excludePaths))
                || (CollectionUtil.isNotEmpty(includePaths) && includePaths.contains(fullPath))
                || (CollectionUtil.isNotEmpty(excludePaths) && !excludePaths.contains(fullPath))) {
            // 替换请求的url
            log.info("修改请求URL,服务:{},替换后的target:{},替换后的url:{}", serviceName, host + prefixPath, host + fullPath);
            requestTemplate.target(host + prefixPath);
        } else {
            log.info("不修改请求URL,服务:{},url:{}", serviceName, fullPath);
        }

    }
}

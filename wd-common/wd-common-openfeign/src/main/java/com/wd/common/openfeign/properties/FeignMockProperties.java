package com.wd.common.openfeign.properties;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * feign:
 *   mock:
 *     enabled: true
 *     servers:
 *       demo-server:
 *         target-url: https://m1.apifoxmock.com/m1/123
 *         include-path:
 *           - /path1
 *           - /path2
 *         exclude-path:
 *           - /path3
 *           - /path4
 * </p>
 * @author huangwenda
 * @date 2023/5/11 14:31
 **/
@ConfigurationProperties(prefix = "feign.mock")
@Configuration
@Getter
@Setter
@ToString
public class FeignMockProperties {

    /**
     * 是否开启mock
     */
    private Boolean enabled;

    /**
     * map<请求服务名, 替换的url>
     */
    private Map<String, AssignUrl> servers;


    @Getter
    @Setter
    @ToString
    public static class AssignUrl {

        /**
         * 目标url
         */
        private String targetUrl;

        /**
         * 包含的路径
         * 优先级：包含的路径 > 排除的路径
         */
        private List<String> includePath;

        /**
         * 排除的路径
         */
        private List<String> excludePath;
    }

    /**
     * 修复nacos动态配置不删除map中元素问题
     */
    @PreDestroy
    public void destroy() {
        servers = null;
    }
}

package com.wd.gateway.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 *
 * @author huangwenda
 * @date 2025/9/16 17:25
 **/
@Configuration
public class NacosMetadataConfig {

    private final NacosDiscoveryProperties nacosDiscoveryProperties;

    public NacosMetadataConfig(NacosDiscoveryProperties nacosDiscoveryProperties) {
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
    }

    @PostConstruct
    public void initMetadata() {
        nacosDiscoveryProperties.getMetadata().put("startup.time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}

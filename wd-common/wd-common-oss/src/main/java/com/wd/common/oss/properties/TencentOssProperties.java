package com.wd.common.oss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 01
 */
@Data
@Component
@ConfigurationProperties(prefix = "tencent.oss")
public class TencentOssProperties {
    private String host;
    private String secretId;
    private String secretKey;
    private String bucket;
    private String region;
    private String stsHost;
    private Integer stsDurationSeconds;
}

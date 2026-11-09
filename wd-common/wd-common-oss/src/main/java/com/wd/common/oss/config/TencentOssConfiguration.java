package com.wd.common.oss.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.endpoint.UserSpecifiedEndpointBuilder;
import com.qcloud.cos.region.Region;
import com.wd.common.oss.properties.TencentOssProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * @author 01
 */
@SpringBootConfiguration
@ConditionalOnClass(COSClient.class)
@RequiredArgsConstructor
public class TencentOssConfiguration {

    private final TencentOssProperties tencentOssProperties;

    public ClientConfig clientConfig() {
        ClientConfig clientConfig = new ClientConfig(new Region(tencentOssProperties.getRegion()));
        // 自定义域名
        UserSpecifiedEndpointBuilder endPointBuilder = new UserSpecifiedEndpointBuilder(tencentOssProperties.getHost(), "service.cos.myqcloud.com");
        clientConfig.setEndpointBuilder(endPointBuilder);
        return clientConfig;
    }

    public COSCredentials cosCredentials() {
        return new BasicCOSCredentials(tencentOssProperties.getSecretId(), tencentOssProperties.getSecretKey());
    }

    @Bean
    public COSClient cosClient() {
        return new COSClient(this.cosCredentials(), this.clientConfig());
    }
}

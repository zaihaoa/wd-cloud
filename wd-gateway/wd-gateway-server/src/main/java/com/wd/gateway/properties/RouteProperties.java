package com.wd.gateway.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 *
 * @author huangwenda
 * @date 2024/4/29 20:48
 **/
@Getter
@Setter
@ToString
@Component
@ConfigurationProperties("route")
public class RouteProperties {

    /**
     * 需要排除的路径
     */
    private List<String> excludePaths;

}

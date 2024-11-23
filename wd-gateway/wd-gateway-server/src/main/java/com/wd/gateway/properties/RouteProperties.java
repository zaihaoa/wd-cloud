package com.wd.gateway.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 *
 * @author huangwenda
 * @date 2024/4/29 20:48
 **/
@Component
@ConfigurationProperties("route")
public class RouteProperties {


    /**
     * 路径版本
     */
    @Getter
    @Setter
    private Integer version;

    /**
     * 需要排除的路径
     */
    @Getter
    @Setter
    private List<Path> excludePaths;

    /**
     * 需要权限的路径
     */
    @Getter
    @Setter
    private List<PermissionPath> permissionPaths;

    /**
     * 需要限流的路径
     */
    @Getter
    @Setter
    private List<Path> rateLimitPaths;

    @Getter
    @Setter
    public static class Path {

        /**
         * 前缀
         */
        private String prefix;

        /**
         * 路径
         */
        private List<String> paths;
    }

    @Getter
    @Setter
    public static class PermissionPath {

        /**
         * 路径
         */
        private String path;

        /**
         * 请求方式
         */
        private String method;

        /**
         * 权限
         */
        private String permission;
    }


    /**
     * 实际版本,与{version}对比刷新数据
     */
    private int actualExcludePathVersion = 0;

    /**
     * 实际排除的路径
     */
    private List<String> acutalExcludeList;

    public List<String> getActualExcludeList() {
        if (!Objects.equals(actualExcludePathVersion, version)) {
            initExcludeList();
        }
        return acutalExcludeList;
    }

    private synchronized void initExcludeList() {

        List<String> acutalExcludeList = new ArrayList<>();

        // 需要排除的路径
        if (!CollectionUtils.isEmpty(excludePaths)) {

            for (Path excludePath : excludePaths) {
                String prefix = excludePath.getPrefix();
                List<String> suffixPaths = excludePath.getPaths();

                if (prefix != null && !prefix.isBlank()) {
                    for (String suffixPath : suffixPaths) {
                        acutalExcludeList.add(prefix + suffixPath);
                    }
                } else {
                    acutalExcludeList.addAll(suffixPaths);
                }
            }
        }
        this.acutalExcludeList = acutalExcludeList;

        this.actualExcludePathVersion = this.version;
    }


    /**
     * 实际版本,与{version}对比刷新数据
     */
    private int actualRateLimitPathVersion = 0;

    /**
     * 实际要限流的路径
     */
    private List<String> actualRateLimitPaths;

    public List<String> getActualRateLimitPaths() {
        if (!Objects.equals(actualRateLimitPathVersion, version)) {
            initRateLimitPaths();
        }
        return actualRateLimitPaths;
    }

    private synchronized void initRateLimitPaths() {

        List<String> actualRateLimitPaths = new ArrayList<>();

        // 需要排除的路径
        if (!CollectionUtils.isEmpty(rateLimitPaths)) {

            for (Path rateLimitPath : rateLimitPaths) {
                String prefix = rateLimitPath.getPrefix();
                List<String> suffixPaths = rateLimitPath.getPaths();

                if (prefix != null && !prefix.isBlank()) {
                    for (String suffixPath : suffixPaths) {
                        actualRateLimitPaths.add(prefix + suffixPath);
                    }
                } else {
                    actualRateLimitPaths.addAll(suffixPaths);
                }
            }
        }
        this.actualRateLimitPaths = actualRateLimitPaths;

        this.actualRateLimitPathVersion = this.version;
    }
}

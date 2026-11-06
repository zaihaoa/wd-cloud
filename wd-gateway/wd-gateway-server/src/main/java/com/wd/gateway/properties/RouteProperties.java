package com.wd.gateway.properties;

import cn.hutool.core.collection.CollUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@ToString
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
        if (CollUtil.isNotEmpty(excludePaths)) {

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

}

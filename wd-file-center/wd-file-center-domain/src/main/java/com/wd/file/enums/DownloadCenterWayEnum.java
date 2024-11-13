package com.wd.file.enums;

import com.wd.common.core.CodeDescEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangwenda
 * @date 2023/8/21 14:55
 **/
@Getter
@AllArgsConstructor
public enum DownloadCenterWayEnum implements CodeDescEnum<String> {
    JOB("JOB", "定时任务"),
    MANUAL("MANUAL", "手动"),
    ;

    private final String code;

    private final String desc;
}

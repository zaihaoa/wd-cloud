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
public enum DownloadCenterStatusEnum implements CodeDescEnum<String> {
    PROCESS("PROCESS", "处理中"),
    SUCCESS("SUCCESS", "成功"),
    FAILURE("FAILURE", "失败"),
    ;

    private final String code;

    private final String desc;
}

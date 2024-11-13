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
public enum AttachmentFileTypeEnum implements CodeDescEnum<String> {

    XLSX("xlsx", "Excel-2007版本"),
    ;

    private final String code;

    private final String desc;
}

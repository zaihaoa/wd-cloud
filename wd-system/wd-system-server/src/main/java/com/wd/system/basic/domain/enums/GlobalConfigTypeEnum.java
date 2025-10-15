package com.wd.system.basic.domain.enums;

import com.wd.common.core.model.CodeDescEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类型枚举
 **/
@Getter
@AllArgsConstructor
public enum GlobalConfigTypeEnum implements CodeDescEnum {
    SYSTEM("SYSTEM", "系统内置"),
    NORMAL("NORMAL", "普通"),
    ;

    private final String code;

    private final String desc;
}

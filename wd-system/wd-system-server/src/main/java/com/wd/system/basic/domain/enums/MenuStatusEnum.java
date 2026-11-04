package com.wd.system.basic.domain.enums;

import com.wd.common.core.model.CodeDescEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuStatusEnum implements CodeDescEnum {
    ENABLED("ENABLED", "启用"),
    DISABLED("DISABLED", "禁用"),
    ;

    private final String code;

    private final String desc;
}

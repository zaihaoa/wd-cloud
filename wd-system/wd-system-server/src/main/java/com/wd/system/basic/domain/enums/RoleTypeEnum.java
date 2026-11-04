package com.wd.system.basic.domain.enums;

import com.wd.common.core.model.CodeDescEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleTypeEnum implements CodeDescEnum {
    NORMAL("NORMAL", "普通"),
    BUILT_IN("BUILT_IN", "内置"),
    ;

    private final String code;

    private final String desc;
}

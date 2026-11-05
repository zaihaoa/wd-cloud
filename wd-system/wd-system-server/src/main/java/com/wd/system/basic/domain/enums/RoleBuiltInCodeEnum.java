package com.wd.system.basic.domain.enums;

import com.wd.common.core.model.CodeDescEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleBuiltInCodeEnum implements CodeDescEnum {
    ADMIN("admin", "超级管理员"),
    ;

    private final String code;

    private final String desc;
}

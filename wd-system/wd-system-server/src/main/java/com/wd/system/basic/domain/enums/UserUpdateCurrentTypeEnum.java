package com.wd.system.basic.domain.enums;

import com.wd.common.core.model.CodeDescEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserUpdateCurrentTypeEnum implements CodeDescEnum {
    NAME("NAME", "姓名"),
    PHONE("PHONE", "手机号"),
    EMAIL("EMAIL", "邮箱");

    private final String code;
    private final String desc;
}

package com.wd.system.system.enums;

import com.wd.common.core.CodeDescEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangwenda
 * @date 2023/1/31 13:54
 **/
@Getter
@AllArgsConstructor
public enum UserStatusEnum implements CodeDescEnum<String> {
    ENABLE("ENABLE", "启用"),
    DISABLE("DISABLE", "禁用");

    private final String code;

    private final String desc;
}

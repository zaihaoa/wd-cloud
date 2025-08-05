package com.wd.system.basic.domain.enums;

import com.wd.common.core.model.CodeDescEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangwenda
 * @date 2024/1/8 11:28
 **/
@Getter
@AllArgsConstructor
public enum UserStatusEnum implements CodeDescEnum {
    NORMAL("NORMAL", "启用"),
    DISABLE("DISABLE", "禁用"),
    ;

    private final String code;

    private final String desc;
}

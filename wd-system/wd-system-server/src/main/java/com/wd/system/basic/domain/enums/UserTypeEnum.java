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
public enum UserTypeEnum implements CodeDescEnum {
    SYSTEM("SYSTEM", "系统用户"),
    NORMAL("NORMAL", "普通用户"),
    ;

    private final String code;

    private final String desc;
}

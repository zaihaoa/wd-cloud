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
public enum CommonLogBusinessTypeEnum implements CodeDescEnum {
    USER("USER", "用户管理"),
    ROLE("ROLE", "角色管理"),
    MENU("MENU", "菜单管理"),

    ;

    private final String code;

    private final String desc;
}

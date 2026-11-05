package com.wd.system.basic.domain.enums;

import com.wd.common.core.model.CodeDescEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RolePermissionTypeEnum implements CodeDescEnum {
    MENU("MENU", "菜单权限"),
    FIELD("FIELD", "字段权限"),
    DATA_USER("DATA_USER", "用户数据权限"),
    ;

    private final String code;

    private final String desc;
}

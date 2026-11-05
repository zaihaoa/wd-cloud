package com.wd.system.basic.domain.enums;

import com.wd.common.core.model.CodeDescEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RolePermissionFlagEnum implements CodeDescEnum {
    ALL_PERMISSION("ALL_PERMISSION", "所有权限"),
    ALL_MENU_PERMISSION("ALL_MENU_PERMISSION", "所有菜单权限"),
    ALL_FIELD_PERMISSION("ALL_FIELD_PERMISSION", "所有字段权限"),
    ALL_DATA_USER_PERMISSION("ALL_DATA_USER_PERMISSION", "所有用户数据权限"),
    ;

    private final String code;

    private final String desc;
}

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
public enum UserTypeEnum implements CodeDescEnum<String> {
    NORMAL("NORMAL", "普通用户"),
    MANAGER("MANAGER", "管理员"),
    SYSTEM_MANAGER("SYSTEM_MANAGER", "系统管理员"),
    SYSTEM_SUPER_ADMIN("SYSTEM_SUPER_ADMIN", "系统超级管理员");

    private final String code;

    private final String desc;
}

package com.wd.common.core.enums;

import com.wd.common.core.constants.FieldPermissionConstant;
import com.wd.common.core.model.CodeDescEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangwenda
 * @date 2023/3/24 13:54
 **/
@Getter
@AllArgsConstructor
public enum FieldPermissionEnum implements CodeDescEnum {
    SYSTEM_USER_PHONE(FieldPermissionConstant.USER_PHONE, "手机号码", "用户"),
    ;

    private final String code;

    private final String desc;

    private final String group;
}

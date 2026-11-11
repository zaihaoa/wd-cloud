package com.wd.system.basic.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RoleAuthorizeOperationEnum {
    GRANT,
    REVOKE;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static RoleAuthorizeOperationEnum fromValue(String value) {
        return valueOf(value);
    }
}

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
public enum UserConfigTypeEnum implements CodeDescEnum {
    CUSTOM_COLUMN("CUSTOM_COLUMN", "自定义列"),
    QUERY("QUERY", "查询条件"),
    ;

    private final String code;

    private final String desc;
}

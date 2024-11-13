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
public enum UserSexEnum implements CodeDescEnum<String> {
    MAN("MAN", "男"),
    WOMAN("WOMAN", "女");

    private final String code;

    private final String desc;
}

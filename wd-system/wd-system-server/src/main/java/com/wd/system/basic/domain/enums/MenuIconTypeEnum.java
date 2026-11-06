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
public enum MenuIconTypeEnum implements CodeDescEnum {
    ICONIFY("ICONIFY", "iconify图标"),
    LOCAL("LOCAL", "本地图标"),
    ;

    private final String code;

    private final String desc;
}

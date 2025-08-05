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
public enum MenuTypeEnum implements CodeDescEnum {
    DIRECTORY("DIRECTORY", "目录"),
    MENU("MENU", "菜单"),
    BUTTON("BUTTON", "按钮"),
    ;

    private final String code;

    private final String desc;
}

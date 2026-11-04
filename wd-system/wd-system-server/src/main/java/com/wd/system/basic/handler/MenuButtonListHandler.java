package com.wd.system.basic.handler;

import com.baomidou.mybatisplus.extension.handlers.Jackson3TypeHandler;
import com.wd.system.basic.domain.model.MenuButton;
import tools.jackson.core.type.TypeReference;

import java.util.List;

/**
 * 菜单 JSON 列表映射，保留元素的具体类型。
 */
public class MenuButtonListHandler extends Jackson3TypeHandler {

    public MenuButtonListHandler(Class<?> type) {
        super(type);
    }

    @Override
    public Object parse(String json) {
        return getObjectMapper().readValue(json, new TypeReference<List<MenuButton>>() {
        });
    }
}

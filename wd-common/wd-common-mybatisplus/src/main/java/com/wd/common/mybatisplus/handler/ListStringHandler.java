package com.wd.common.mybatisplus.handler;

import com.baomidou.mybatisplus.extension.handlers.Jackson3TypeHandler;
import tools.jackson.core.type.TypeReference;

import java.util.List;

/**
 * @author huangwenda
 * @date 2024/2/9 17:38
 **/
public class ListStringHandler extends Jackson3TypeHandler {

    public ListStringHandler(Class<?> type) {
        super(type);
    }

    @Override
    public Object parse(String json) {
        return getObjectMapper().readValue(json, new TypeReference<List<String>>() {
        });
    }
}

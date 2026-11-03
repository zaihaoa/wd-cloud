package com.wd.common.mybatisplus.handler;

import com.baomidou.mybatisplus.extension.handlers.Jackson3TypeHandler;
import tools.jackson.core.type.TypeReference;

import java.util.List;

/**
 * @author huangwenda
 * @date 2024/2/9 17:38
 **/
public class ListIntegerHandler extends Jackson3TypeHandler {

    public ListIntegerHandler(Class<?> type) {
        super(type);
    }

    @Override
    public Object parse(String json) {
        return Jackson3TypeHandler.getObjectMapper().readValue(json, new TypeReference<List<Integer>>() {
        });
    }
}

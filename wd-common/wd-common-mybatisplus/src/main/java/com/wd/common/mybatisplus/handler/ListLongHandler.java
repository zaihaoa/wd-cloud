package com.wd.common.mybatisplus.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;

/**
 * @author huangwenda
 * @date 2024/2/9 17:38
 **/
public class ListLongHandler extends JacksonTypeHandler {

    public ListLongHandler(Class<?> type) {
        super(type);
    }

    @Override
    public Object parse(String json) {
        try {
            return JacksonTypeHandler.getObjectMapper().readValue(json, new TypeReference<List<Long>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

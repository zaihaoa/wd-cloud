package com.wd.common.mybatisplus.log;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;
import lombok.extern.slf4j.Slf4j;

/**
 *
 *
 * @author huangwenda
 * @date 2025/9/15 17:42
 **/
@Slf4j
public class Slf4JSqlParamLogger extends FormattedLogger {

    @Override
    public void logException(Exception e) {
        log.info("", e);
    }

    @Override
    public void logText(String text) {
        log.info(text);
    }

    @Override
    public boolean isCategoryEnabled(Category category) {
        return log.isInfoEnabled();
    }

}

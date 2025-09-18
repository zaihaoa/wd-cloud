package com.wd.common.mybatisplus.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.Log;

/**
 *
 *
 * @author huangwenda
 * @date 2025/9/15 17:42
 **/
@Slf4j
public class StdOutSqlResultImpl implements Log {

    public StdOutSqlResultImpl(String clazz) {
    }

    public boolean isDebugEnabled() {
        return true;
    }

    public boolean isTraceEnabled() {
        return true;
    }

    public void error(String s, Throwable e) {
        System.err.println(s);
        e.printStackTrace(System.err);
    }

    public void error(String s) {
        System.err.println(s);
    }

    public void debug(String s) {
    }

    public void trace(String s) {
        System.err.println(s);
    }

    public void warn(String s) {
        System.err.println(s);
    }
}

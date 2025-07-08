package com.wd.common.core.util;

import com.wd.common.core.constants.CommonConstant;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author huangwenda
 * @date 2023/4/17 18:44
 **/
public class MdcUtil {
    /**
     * 获取traceExtra中的值
     *
     * @return traceExtra
     */
    public static String getTraceExtra() {
        return MDC.get(CommonConstant.TRACE_EXTRA);
    }

    /**
     * 设置traceExtra的值
     */
    public static void setTraceExtra(String traceExtra) {
        if (StringUtils.hasText(traceExtra)) {
            MDC.put(CommonConstant.TRACE_EXTRA, traceExtra);
        }
    }

    /**
     * 设置traceExtra的值
     */
    public static void setTraceExtra(String traceExtra1, String traceExtra2) {
        if (StringUtils.hasText(traceExtra1) && StringUtils.hasText(traceExtra2)) {
            MDC.put(CommonConstant.TRACE_EXTRA, String.format("%s,%s", traceExtra1, traceExtra2));
            return;
        }
        if (StringUtils.hasText(traceExtra1)) {
            MDC.put(CommonConstant.TRACE_EXTRA, traceExtra1);
            return;
        }
        if (StringUtils.hasText(traceExtra2)) {
            MDC.put(CommonConstant.TRACE_EXTRA, traceExtra2);
            return;
        }
    }

    /**
     * 设置traceExtra的值
     */
    public static void setTraceExtra(String... traceExtra) {
        if (Objects.nonNull(traceExtra) && traceExtra.length > 0) {
            MDC.put(CommonConstant.TRACE_EXTRA, String.join(",", traceExtra));
        }
    }

    /**
     * 清除traceExtra的值
     */
    public static void removeTraceExtra() {
        MDC.remove(CommonConstant.TRACE_EXTRA);
    }
}

package com.wd.common.redis.util;


import com.wd.common.redis.CacheRegion;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CodeGeneratorHelper {

    @Resource
    private RedisHelper redisHelper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String codeGenerator(String codePrefix) {
        return codeGenerator(codePrefix, LocalDate.now().format(DATE_FORMATTER), 4);
    }

    public String codeGenerator(String codePrefix, int suffixDigit) {
        return codeGenerator(codePrefix, LocalDate.now().format(DATE_FORMATTER), suffixDigit);
    }

    public String codeGenerator(String codePrefix, String codeMiddle) {
        return codeGenerator(codePrefix, codeMiddle, 4);
    }

    /**
     * 生成编码字符串
     *
     * @param codePrefix 编码前缀
     * @param codeMiddle 编码中间部分
     * @param suffixDigit 后缀数字位数
     * @return 生成的完整编码字符串
     */
    public String codeGenerator(String codePrefix, String codeMiddle, int suffixDigit) {

        String redisKey = RedisHelper.joinKey(codePrefix, codeMiddle);
        long incr = redisHelper.increment(CacheRegion.CODE_GENERATOR, redisKey, 1);

        if (incr <= 2) {
            // 设置超时时间2天
            redisHelper.setExpire(CacheRegion.CODE_GENERATOR, redisKey, 2, TimeUnit.DAYS);
        }

        // 构造格式化字符串，如%04d表示最少4位，不足补0
        String format = "%0" + suffixDigit + "d";
        long threshold = (long) Math.pow(10, suffixDigit - 1);
        String codeSuffix = incr <= threshold ? String.format(format, incr) : String.valueOf(incr);

        return codePrefix + codeMiddle + codeSuffix;
    }
}

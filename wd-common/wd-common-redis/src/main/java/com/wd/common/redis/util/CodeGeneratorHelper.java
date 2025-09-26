package com.wd.common.redis.util;


import com.wd.common.redis.CacheRegion;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CodeGeneratorHelper {

    @Resource
    private RedisHelper redisHelper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 编号生成
     * 默认后面填充4位,incr不足4位则填充0,incr超过4位则直接拼接
     * CS202301010001 -> CS202301010002 -> CS202301010002 -> CS2023010110001 -> CS2023010110002
     *
     * @param codePrefix 编号前缀
     * @return 编号前缀 + 8位日期 + 4位以上编号
     */
    public String codeGenerator(String codePrefix) {
        String nowDate = LocalDateTime.now().format(DATE_FORMATTER);

        String redisKey = RedisHelper.joinKey(codePrefix, nowDate);
        long incr = redisHelper.increment(CacheRegion.CODE_GENERATOR, redisKey, 1);

        if (incr <= 5) {
            // 设置超时时间2天
            redisHelper.setExpire(CacheRegion.CODE_GENERATOR, redisKey, 2, TimeUnit.DAYS);
        }

        String codeSuffix = incr <= 999 ? String.format("%04d", incr) : String.valueOf(incr);
        return codePrefix + nowDate + codeSuffix;
    }
}

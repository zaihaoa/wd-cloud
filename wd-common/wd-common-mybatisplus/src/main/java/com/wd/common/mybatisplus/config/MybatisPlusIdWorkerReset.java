package com.wd.common.mybatisplus.config;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.RedisKey;
import com.wd.common.redis.util.RedisHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author huangwenda
 **/
@Slf4j
@Component
public class MybatisPlusIdWorkerReset implements ApplicationRunner {

    @Resource
    private RedisHelper redisHelper;

    /**
     * sequence的取值范围[0,1023]
     * 把sequence转为二进制,1023的二进制最多为10位(1111111111)
     * 把二进制前5位作为workId,后5位作为dataId
     */
    @Override
    public void run(ApplicationArguments args) {
        try {
            // 序列化数值
            long workId = 0;
            long dataId = 0;

            // 从redis获取存储的workId的值
            Long redisValue = redisHelper.increment(CacheRegion.MYBATIS_PLUS, RedisKey.MYBATIS_PLUS_ID_WORKER);
            // 对1024取余
            long sequence = redisValue % 1024;

            String binaryString = Long.toBinaryString(sequence);
            int length = binaryString.length();

            int delimiterIndex = 5;
            if (length <= delimiterIndex) {
                dataId = Long.parseUnsignedLong(binaryString, 2);
            } else {
                workId = Long.parseUnsignedLong(binaryString.substring(0, length - delimiterIndex), 2);
                dataId = Long.parseUnsignedLong(binaryString.substring(length - delimiterIndex, length), 2);
            }


            IdWorker.initSequence(workId, dataId);
            log.info("重置MybatisPlus的IdWorker成功,redis值:{},sequence:{},workId:{},dataId:{}", redisValue, sequence, workId, dataId);
        } catch (Exception e) {
            log.error("重置MybatisPlus的IdWorker失败", e);
        }
    }
}

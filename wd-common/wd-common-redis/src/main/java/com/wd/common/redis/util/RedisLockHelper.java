package com.wd.common.redis.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author huangwenda
 * @date 2024/2/1 15:36
 **/
@Slf4j
@Component
public class RedisLockHelper {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * redis分布式锁
     * @param lockKey key
     * @param waitTime 最大等待加锁时间
     * @param leaseTime 加锁之后锁过期时间
     * @param unit 时间单位
     * @param supplier 业务执行代码
     * @return 业务执行代码返回
     */
    @SneakyThrows
    public <T> T lock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> supplier) {
        // 锁对象
        RLock lock = redissonClient.getLock(lockKey);
        // 加锁，最大等待加锁时间{waitTime}秒,加锁之后锁过期时间{leaseTime}秒
        if (lock.tryLock(waitTime, leaseTime, unit)) {
            try {
                return supplier.get();
            } finally {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.error("释放锁报错", e);
                }
            }
        }
        log.warn("未获取到锁");
        return null;
    }
}

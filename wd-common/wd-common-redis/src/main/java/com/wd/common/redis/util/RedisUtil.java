package com.wd.common.redis.util;

import com.wd.common.redis.constant.RedisConstant;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * redis工具类
 * 交给spring管理
 *
 * @author huangwenda
 * @date 2022/7/8 16:38
 **/
@Component
public class RedisUtil {

    /**
     * 通过{RedisConstant.KEY_SPLIT}拼接redis的key
     *
     * @param keys 各个redis key
     * @return 拼接之后的redis key
     */
    public static String joinKey(String... keys) {
        if (Objects.isNull(keys) || keys.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        // 长度
        int length = keys.length;
        // 最大索引
        int maxIndex = keys.length - 1;
        for (int i = 0; i < length; i++) {
            sb.append(keys[i]);
            if (i != maxIndex) {
                sb.append(RedisConstant.KEY_JOIN);
            }
        }
        return sb.toString();
    }


    /**
     * 生成随机数
     * @param min 最小值
     * @param max 最大值
     * @return [min, max]
     */
    public static long random(long min, long max) {
        return min + (long) (Math.random() * (max - min + 1));
    }
}

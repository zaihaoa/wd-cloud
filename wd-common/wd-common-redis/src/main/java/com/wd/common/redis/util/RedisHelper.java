package com.wd.common.redis.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.wd.common.redis.CacheRegion;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * redis工具类
 * 交给spring管理
 *
 * @author huangwenda
 * @date 2022/7/8 16:38
 **/
@Slf4j
@Component
public class RedisHelper {

    /**
     * 分隔符
     */
    public static final String SPLIT = ":";

    /**
     * 空对象缓存值
     */
    public static final String EMPTY_OBJECT = "EmptyObject20240116";

    /**
     * 对象转json
     */
    public static final ObjectMapper OM = redisSerializationObjectMapper();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public static ObjectMapper redisSerializationObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 解决jackson2无法反序列化LocalDateTime的问题
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new ParameterNamesModule());
        om.registerModule(new Jdk8Module());

        om.registerModule(new JavaTimeModule());

        // 忽略null字段
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 支持序列化没有字段的空对象
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        return om;
    }


    public void set(CacheRegion cacheRegion, String key, Object val) {
        stringRedisTemplate.opsForValue().set(regionJoinKey(cacheRegion, key), val != null ? toString(val) : EMPTY_OBJECT);
    }


    public void setEx(CacheRegion cacheRegion, String key, Object val, long minTimeout, long maxTimeout, TimeUnit unit) {
        setEx(cacheRegion, key, val, random(minTimeout, maxTimeout), unit);
    }

    public void setEx(CacheRegion cacheRegion, String key, Object val, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(
                regionJoinKey(cacheRegion, key),
                val != null ? toString(val) : EMPTY_OBJECT,
                timeout,
                unit);
    }

    public <T> T get(CacheRegion cacheRegion, String key, Class<T> o) {
        String val = stringRedisTemplate.opsForValue().get(regionJoinKey(cacheRegion, key));
        if (isCacheEmpty(val)) {
            return null;
        }
        return parse(val, o);
    }

    public <T> T get(CacheRegion cacheRegion, String key, TypeReference<T> valueTypeRef) {
        String val = stringRedisTemplate.opsForValue().get(regionJoinKey(cacheRegion, key));
        if (isCacheEmpty(val)) {
            return null;
        }
        return parse(val, valueTypeRef);
    }

    public Long getExpire(CacheRegion cacheRegion, String key) {
        return stringRedisTemplate.opsForValue().getOperations().getExpire(regionJoinKey(cacheRegion, key));
    }

    public Long getExpire(CacheRegion cacheRegion, String key, TimeUnit timeUnit) {
        return stringRedisTemplate.opsForValue().getOperations().getExpire(regionJoinKey(cacheRegion, key), timeUnit);
    }

    public boolean setExpire(CacheRegion cacheRegion, String key, long timeout, TimeUnit unit) {
        Boolean expire = stringRedisTemplate.expire(regionJoinKey(cacheRegion, key), timeout, unit);
        return Boolean.TRUE.equals(expire);
    }

    public String getString(CacheRegion cacheRegion, String key) {
        return stringRedisTemplate.opsForValue().get(regionJoinKey(cacheRegion, key));
    }

    public <T> Map<String, T> multiGet(CacheRegion cacheRegion, Collection<String> keys, Class<T> o) {
        List<String> fullKeys = keys.stream().map(v -> regionJoinKey(cacheRegion, v)).toList();
        List<String> values = stringRedisTemplate.opsForValue().multiGet(fullKeys);

        int size = fullKeys.size();
        Map<String, T> result = new HashMap<>(size);

        for (int i = 0; i < size; i++) {
            String val = values.get(i);
            if (!isCacheEmpty(val)) {
                T obj = parse(val, o);
                if (obj != null) {
                    result.put(fullKeys.get(i), obj);
                }
            }
        }

        return result;
    }

    public <T> Map<String, T> multiGet(CacheRegion cacheRegion, Collection<String> keys, TypeReference<T> valueTypeRef) {
        List<String> fullKeys = keys.stream().map(v -> regionJoinKey(cacheRegion, v)).toList();
        List<String> values = stringRedisTemplate.opsForValue().multiGet(fullKeys);

        int size = fullKeys.size();
        Map<String, T> result = new HashMap<>(size);

        for (int i = 0; i < size; i++) {
            String val = values.get(i);
            if (!isCacheEmpty(val)) {
                T obj = parse(val, valueTypeRef);
                if (obj != null) {
                    result.put(fullKeys.get(i), obj);
                }
            }
        }

        return result;
    }

    public void hashPut(CacheRegion cacheRegion, String key, String hashKey, Object hashValue) {
        stringRedisTemplate.<String, String>opsForHash().put(
                regionJoinKey(cacheRegion, key),
                hashKey,
                hashValue != null ? toString(hashValue) : EMPTY_OBJECT);
    }

    public void hashPutAll(CacheRegion cacheRegion, String key, Map entry) {
        Map<String, String> stringEntry = new HashMap<>(entry.size());
        entry.forEach((k, v) -> {
            stringEntry.put(toString(k), v != null ? toString(v) : EMPTY_OBJECT);
        });

        stringRedisTemplate.<String, String>opsForHash().putAll(regionJoinKey(cacheRegion, key), stringEntry);
    }

    public <T> T hashGet(CacheRegion cacheRegion, String key, Object hashKey, Class<T> o) {
        String val = stringRedisTemplate.<String, String>opsForHash().get(regionJoinKey(cacheRegion, key), toString(hashKey));
        if (isCacheEmpty(val)) {
            return null;
        }
        return parse(val, o);
    }

    public boolean hasKey(CacheRegion cacheRegion, String key) {
        Boolean b = stringRedisTemplate.hasKey(regionJoinKey(cacheRegion, key));
        return Boolean.TRUE.equals(b);
    }

    public boolean delete(CacheRegion cacheRegion, String key) {
        Boolean b = stringRedisTemplate.delete(regionJoinKey(cacheRegion, key));
        return b != null ? b : false;
    }

    public long delete(CacheRegion cacheRegion, Collection<String> keys) {
        Long l = stringRedisTemplate.delete(keys.stream().map(v -> regionJoinKey(cacheRegion, v)).toList());
        return l != null ? l : 0L;
    }

    public long deleteByCacheRegion(CacheRegion cacheRegion) {
        return deleteByPattern(cacheRegion.getCode() + ":*");
    }

    public long deleteByCacheRegion(CacheRegion cacheRegion, String keyPrefix) {
        return deleteByPattern(regionJoinKey(cacheRegion, keyPrefix) + "*");
    }

    private long deleteByPattern(String pattern) {
        log.info("批量删除,参数:{}", pattern);
        Set<String> keys = new HashSet<>();
        long delNum = 0L;
        // 获取所有的key
        try (Cursor<String> scan = stringRedisTemplate.scan(ScanOptions.scanOptions().match(pattern).count(5000).build())) {
            while (scan.hasNext()) {
                keys.add(scan.next());

                // 批量删除
                if (keys.size() >= 1000) {
                    log.info("批量删除,数量:{},keys:{}", keys.size(), keys);
                    Long num = stringRedisTemplate.delete(keys);
                    delNum += (num != null ? num : 0L);
                    keys.clear();
                }
            }
        } catch (Exception e) {
            log.error("redis scan命令报错", e);
            throw new RuntimeException(e);
        }
        if (!keys.isEmpty()) {
            log.info("批量删除,数量:{},keys:{}", keys.size(), keys);
            Long num = stringRedisTemplate.delete(keys);
            delNum += (num != null ? num : 0L);
        }
        log.info("批量删除,删除总数量:{}", delNum);
        return delNum;
    }


    public long addSet(CacheRegion cacheRegion, String key, String value) {
        Long l = stringRedisTemplate.opsForSet().add(regionJoinKey(cacheRegion, key), value);
        return l != null ? l : 0L;
    }

    public boolean isMember(CacheRegion cacheRegion, String key, String value) {
        return Optional.ofNullable(stringRedisTemplate.opsForSet().isMember(regionJoinKey(cacheRegion, key), value))
                .orElse(false);
    }

    public List<String> memberValues(CacheRegion cacheRegion, String key, List<String> values) {
        Set<String> allValues = stringRedisTemplate.opsForSet().members(regionJoinKey(cacheRegion, key));
        if (allValues == null) {
            return List.of();
        }
        return values
                .stream()
                .filter(allValues::contains)
                .toList();
    }

    public long removeSet(CacheRegion cacheRegion, String key, String value) {
        Long l = stringRedisTemplate.opsForSet().remove(regionJoinKey(cacheRegion, key), value);
        return l != null ? l : 0L;
    }

    public long addIfAbsentZset(CacheRegion cacheRegion, String key, Collection<String> values) {
        Set<ZSetOperations.TypedTuple<String>> tupleSet = values
                .stream()
                .map(v -> new DefaultTypedTuple<>(v, 0D))
                .collect(Collectors.toSet());
        Long l = stringRedisTemplate.opsForZSet().addIfAbsent(regionJoinKey(cacheRegion, key), tupleSet);
        return l != null ? l : 0L;
    }

    public long addIfAbsentScopeZset(CacheRegion cacheRegion, String key, Set<ZSetOperations.TypedTuple<String>> tuples) {
        Long l = stringRedisTemplate.opsForZSet().addIfAbsent(regionJoinKey(cacheRegion, key), tuples);
        return l != null ? l : 0L;
    }

    public Set<String> rangeZset(CacheRegion cacheRegion, String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(regionJoinKey(cacheRegion, key), start, end);
    }

    public Set<String> rangeScoreZset(CacheRegion cacheRegion, String key, double start, double end) {
        return stringRedisTemplate.opsForZSet().rangeByScore(regionJoinKey(cacheRegion, key), start, end);
    }

    public String firstZset(CacheRegion cacheRegion, String key) {
        Set<String> set = stringRedisTemplate.opsForZSet().range(regionJoinKey(cacheRegion, key), 0L, 1L);
        if (set == null || set.isEmpty()) {
            return null;
        }
        for (String s : set) {
            return s;
        }
        return null;
    }

    public long removeZset(CacheRegion cacheRegion, String key, Object... values) {
        Long l = stringRedisTemplate.opsForZSet().remove(regionJoinKey(cacheRegion, key), values);
        return l != null ? l : 0L;
    }

    public long leftPush(CacheRegion cacheRegion, String key, Collection<String> values) {
        Long l = stringRedisTemplate.opsForList().leftPushAll(regionJoinKey(cacheRegion, key), values);
        return l != null ? l : 0L;
    }

    public String rightPop(CacheRegion cacheRegion, String key) {
        return stringRedisTemplate.opsForList().rightPop(regionJoinKey(cacheRegion, key));
    }

    public long listRemove(CacheRegion cacheRegion, String key, String value) {
        Long l = stringRedisTemplate.opsForList().remove(regionJoinKey(cacheRegion, key), 0, value);
        return l != null ? l : 0L;
    }

    public boolean existList(CacheRegion cacheRegion, String key, String value) {
        Long l = stringRedisTemplate.opsForList().indexOf(regionJoinKey(cacheRegion, key), value);
        return l != null && l != -1;
    }

    /**
     * 自增值
     */
    public Long increment(CacheRegion cacheRegion, String key, int delta) {
        return stringRedisTemplate.opsForValue().increment(regionJoinKey(cacheRegion, key), (long) delta);
    }

    /**
     * 自增值
     */
    public Long increment(CacheRegion cacheRegion, String key) {
        return stringRedisTemplate.opsForValue().increment(regionJoinKey(cacheRegion, key));
    }

    /**
     * 获取自增值
     */
    public Long getIncrementValue(CacheRegion cacheRegion, String key) {
        return get(cacheRegion, key, Long.class);
    }


    /**
     * 生成随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return [min, max]
     */
    public static long random(long min, long max) {
        return min + (long) (Math.random() * (max - min + 1));
    }


    @SneakyThrows
    public static <T> T parse(String str, Class<T> o) {
        if (str == null) {
            return null;
        }
        return OM.readValue(str, o);
    }

    @SneakyThrows
    public static <T> T parse(String str, TypeReference<T> valueTypeRef) {
        if (str == null) {
            return null;
        }
        return OM.readValue(str, valueTypeRef);
    }

    @SneakyThrows
    public static String toString(Object o) {
        return OM.writeValueAsString(o);
    }


    /**
     * 是否缓存的空对象
     *
     * @param o 缓存的值
     * @return 是否缓存的空对象
     */
    public static boolean isCacheEmpty(String o) {
        return EMPTY_OBJECT.equals(o);
    }

    private static String regionJoinKey(CacheRegion cacheRegion, String key) {
        return String.format("%s:%s", cacheRegion.getCode(), key);
    }

    /**
     * 通过{@link RedisHelper#SPLIT}拼接redis的key
     *
     * @param keys 各个redis key
     * @return 拼接之后的redis key
     */
    public static String joinKey(Object... keys) {
        StringBuilder sb = new StringBuilder();

        // 长度
        int length = keys.length;
        // 最大索引
        int maxIndex = keys.length - 1;
        for (int i = 0; i < length; i++) {
            sb.append(keys[i]);
            if (i != maxIndex) {
                sb.append(SPLIT);
            }
        }
        return sb.toString();
    }
}

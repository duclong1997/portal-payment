package com.ezpay.main.redis.repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author OI
 */
public interface RedisRepository<T> {
    void putMap(String redisKey, Object key, T data);

    T getMapAsSingleEntry(String redisKey, Object key);

    Map<Object, T> getMapAsAll(String redisKey);

    void putValue(String key, T value);

    void putValueWithExpireTime(String key, T value, long timeout, TimeUnit unit);

    T getValue(String key);

    void setExpire(String key, long timeout, TimeUnit unit);

    public Long delete(String key, Object id);

    boolean hasKey(String key, Object id);
}

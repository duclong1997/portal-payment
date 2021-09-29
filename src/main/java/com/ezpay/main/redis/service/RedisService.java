package com.ezpay.main.redis.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface RedisService<T> {
    void putMap(Object key, T data);

    T getMapAsSingleEntry(Object key);

    Map<Object, T> getMapAsAll();

    void putValue(String key, T value);

    void putValueWithExpireTime(String key, T value, long timeout, TimeUnit unit);

    T getValue(String key);

    void setExpire(String key, long timeout, TimeUnit unit);

    public Long delete(Object id);

    boolean hasKey(Object id);
}

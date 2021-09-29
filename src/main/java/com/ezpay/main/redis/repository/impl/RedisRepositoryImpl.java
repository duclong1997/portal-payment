package com.ezpay.main.redis.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import com.ezpay.main.redis.repository.RedisRepository;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author OI
 */
@Repository
public class RedisRepositoryImpl<T> implements RedisRepository<T> {
    private RedisTemplate<String, T> redisTemplate;
    private HashOperations<String, Object, T> hashOperation;
    private ListOperations<String, T> listOperation;
    private ValueOperations<String, T> valueOperations;

    @Autowired
    public RedisRepositoryImpl(RedisTemplate<String, T> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperation = redisTemplate.opsForHash();
        this.listOperation = redisTemplate.opsForList();
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void putMap(String redisKey, Object key, T data) {
        hashOperation.put(redisKey, key, data);
    }

    @Override
    public T getMapAsSingleEntry(String redisKey, Object key) {
        return hashOperation.get(redisKey, key);
    }

    @Override
    public Map<Object, T> getMapAsAll(String redisKey) {
        return hashOperation.entries(redisKey);
    }

    @Override
    public void putValue(String key, T value) {
        valueOperations.set(key, value);
    }

    @Override
    public void putValueWithExpireTime(String key, T value, long timeout, TimeUnit unit) {
        valueOperations.set(key, value, timeout, unit);
    }

    @Override
    public T getValue(String key) {
        return valueOperations.get(key);
    }

    @Override
    public void setExpire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    @Override
    public Long delete(String key, Object id) {
        return hashOperation.delete(key, id);
    }

    @Override
    public boolean hasKey(String key, Object id) {
        return hashOperation.hasKey(key, id);
    }
}

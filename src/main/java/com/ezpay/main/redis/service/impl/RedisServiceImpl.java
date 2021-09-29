package com.ezpay.main.redis.service.impl;

import com.ezpay.main.redis.repository.RedisRepository;
import com.ezpay.main.redis.service.RedisService;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RedisServiceImpl<T> implements RedisService<T> {
    private RedisRepository<T> repository;
    private Class<T> eclass;

    public RedisServiceImpl(RedisRepository<T> repository, Class<T> eclass) {
        this.repository = repository;
        this.eclass = eclass;
    }

    @Override
    public void putMap(Object key, T data) {
        repository.putMap(eclass.getName(), key, data);
    }

    @Override
    public T getMapAsSingleEntry(Object key) {
        return repository.getMapAsSingleEntry(eclass.getName(), key);
    }

    @Override
    public Map<Object, T> getMapAsAll() {
        return repository.getMapAsAll(eclass.getName());
    }

    @Override
    public void putValue(String key, T value) {
        repository.putValue(key, value);
    }

    @Override
    public void putValueWithExpireTime(String key, T value, long timeout, TimeUnit unit) {
        repository.putValueWithExpireTime(key, value, timeout, unit);
    }

    @Override
    public T getValue(String key) {
        return repository.getValue(key);
    }

    @Override
    public void setExpire(String key, long timeout, TimeUnit unit) {
        repository.setExpire(key, timeout, unit);
    }

    @Override
    public Long delete(Object id) {
        return repository.delete(eclass.getName(), id);
    }

    @Override
    public boolean hasKey(Object id) {
        return repository.hasKey(eclass.getName(), id);
    }
}

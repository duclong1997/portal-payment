package com.ezpay.main.redis.service.impl;

import com.ezpay.core.entity.MerchantProject;
import com.ezpay.main.redis.repository.RedisRepository;
import com.ezpay.main.redis.service.MerchantProjectCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantProjectCacheServiceImpl extends RedisServiceImpl<MerchantProject> implements MerchantProjectCacheService {

    private RedisRepository<MerchantProject> repository;

    @Autowired
    public MerchantProjectCacheServiceImpl(RedisRepository<MerchantProject> repository) {
        super(repository, MerchantProject.class);
        this.repository = repository;
    }
}

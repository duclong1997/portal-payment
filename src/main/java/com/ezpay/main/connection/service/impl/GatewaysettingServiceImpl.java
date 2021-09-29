package com.ezpay.main.connection.service.impl;

import com.ezpay.core.entity.Gatewaysetting;
import com.ezpay.core.service.impl.BaseServiceImpl;
import com.ezpay.main.connection.repository.GatewaysettingRepository;
import com.ezpay.main.connection.service.GatewaysettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GatewaysettingServiceImpl extends BaseServiceImpl<Gatewaysetting, Integer> implements GatewaysettingService {
    private GatewaysettingRepository repository;

    @Autowired
    public GatewaysettingServiceImpl(GatewaysettingRepository repository) {
        super(repository);
        this.repository = repository;
    }
}

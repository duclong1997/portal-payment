package com.ezpay.main.connection.service.impl;

import com.ezpay.core.entity.Gateway;
import com.ezpay.core.service.impl.BaseServiceImpl;
import com.ezpay.main.connection.repository.GatewayRepository;
import com.ezpay.main.connection.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GatewayServiceImpl extends BaseServiceImpl<Gateway, String> implements GatewayService {
    private GatewayRepository repository;

    @Autowired
    public GatewayServiceImpl(GatewayRepository repository) {
        super(repository);
        this.repository = repository;
    }
}

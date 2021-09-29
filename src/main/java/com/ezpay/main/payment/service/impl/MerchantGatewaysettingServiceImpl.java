package com.ezpay.main.payment.service.impl;

import com.ezpay.core.entity.MerchantGatewaysetting;
import com.ezpay.core.service.impl.BaseServiceImpl;
import com.ezpay.main.payment.repository.MerchantGatewaysettingRepository;
import com.ezpay.main.payment.service.MerchantGatewaysettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MerchantGatewaysettingServiceImpl extends BaseServiceImpl<MerchantGatewaysetting, Integer> implements MerchantGatewaysettingService {
    private MerchantGatewaysettingRepository repository;

    @Autowired
    public MerchantGatewaysettingServiceImpl(MerchantGatewaysettingRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<MerchantGatewaysetting> getKeyByMerchantGateway(int merchantGatewayId) {
        return repository.getKeyByMerchantGateway(merchantGatewayId);
    }
}

package com.ezpay.main.payment.service.impl;

import com.ezpay.core.entity.MerchantGateway;
import com.ezpay.core.service.impl.BaseServiceImpl;
import com.ezpay.main.payment.repository.MerchantGatewayRepository;
import com.ezpay.main.payment.service.MerchantGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MerchantGatewayServiceImpl extends BaseServiceImpl<MerchantGateway, Integer> implements MerchantGatewayService {
    private MerchantGatewayRepository repository;

    @Autowired
    public MerchantGatewayServiceImpl(MerchantGatewayRepository repository) {
        super(repository);
        this.repository = repository;
    }


    @Override
    public Optional<MerchantGateway> getByMerchantAndGateway(int merchantProjectId, String gatewayCode) {
        return repository.getByMerchantAndGateway(merchantProjectId, gatewayCode);
    }

    @Override
    public List<MerchantGateway> findAlLByMerchantProjectId(int merchantProjectId) {
        return repository.findAlLByMerchantProjectId(merchantProjectId);
    }
}

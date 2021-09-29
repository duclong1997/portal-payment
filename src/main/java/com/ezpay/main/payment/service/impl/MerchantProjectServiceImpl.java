package com.ezpay.main.payment.service.impl;

import com.ezpay.core.entity.MerchantProject;
import com.ezpay.core.service.impl.BaseServiceImpl;
import com.ezpay.main.payment.repository.MerchantProjectRepository;
import com.ezpay.main.payment.service.MerchantProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MerchantProjectServiceImpl extends BaseServiceImpl<MerchantProject, Integer> implements MerchantProjectService {
    private MerchantProjectRepository repository;

    @Autowired
    public MerchantProjectServiceImpl(MerchantProjectRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<MerchantProject> getByConnectId(String connectId) {
        return repository.getByConnectId(connectId);
    }
}

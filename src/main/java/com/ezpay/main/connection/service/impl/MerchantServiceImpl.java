package com.ezpay.main.connection.service.impl;

import com.ezpay.core.entity.Merchant;
import com.ezpay.core.service.impl.BaseServiceImpl;
import com.ezpay.main.connection.repository.MerchantRepository;
import com.ezpay.main.connection.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MerchantServiceImpl extends BaseServiceImpl<Merchant, Integer> implements MerchantService {
    private MerchantRepository repository;

    @Autowired
    public MerchantServiceImpl(MerchantRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<Merchant> getByTaxCode(String taxCode) {
        return repository.getByTaxCode(taxCode);
    }
}

package com.ezpay.main.connection.service;

import com.ezpay.core.entity.Merchant;
import com.ezpay.core.service.BaseService;

import java.util.Optional;

public interface MerchantService extends BaseService<Merchant, Integer> {
    Optional<Merchant> getByTaxCode(String taxCode);
}

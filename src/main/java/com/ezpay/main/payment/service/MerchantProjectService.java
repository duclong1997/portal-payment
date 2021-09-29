package com.ezpay.main.payment.service;

import com.ezpay.core.entity.MerchantProject;
import com.ezpay.core.service.BaseService;

import java.util.Optional;

public interface MerchantProjectService extends BaseService<MerchantProject, Integer> {
    Optional<MerchantProject> getByConnectId(String connectId);
}

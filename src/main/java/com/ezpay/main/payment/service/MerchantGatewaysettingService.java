package com.ezpay.main.payment.service;

import com.ezpay.core.entity.MerchantGatewaysetting;
import com.ezpay.core.service.BaseService;

import java.util.Optional;

public interface MerchantGatewaysettingService extends BaseService<MerchantGatewaysetting, Integer> {
    Optional<MerchantGatewaysetting> getKeyByMerchantGateway(int merchantGatewayId);
}

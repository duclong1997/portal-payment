package com.ezpay.main.payment.service;

import com.ezpay.core.entity.MerchantGateway;
import com.ezpay.core.service.BaseService;

import java.util.List;
import java.util.Optional;

public interface MerchantGatewayService extends BaseService<MerchantGateway, Integer> {

    Optional<MerchantGateway> getByMerchantAndGateway(int merchantProjectId, String gatewayCode);

    List<MerchantGateway> findAlLByMerchantProjectId(int merchantProjectId);
}

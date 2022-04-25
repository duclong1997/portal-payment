package com.ezpay.core.gateway;

import com.ezpay.core.entity.MerchantGatewaysetting;

import java.util.List;
import java.util.Map;

/**
 * @author OI
 */
public interface Payment {
    boolean checkFields(Map<String, String> fields, String key);

    String getPaymentLink(List<MerchantGatewaysetting> params, String payUrl);

    String getResponseDescription(String vResponseCode);

    String hashKey(Map<String, String> fields, String key);

    Map<String, String> getFieldsValues(List<MerchantGatewaysetting> params);
}

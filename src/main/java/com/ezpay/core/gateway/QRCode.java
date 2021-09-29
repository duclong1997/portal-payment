package com.ezpay.core.gateway;

import com.ezpay.core.entity.MerchantGatewaysetting;
import com.ezpay.core.gateway.model.QrcodeResponse;

import java.util.List;
import java.util.Map;

/**
 * @author OI
 */
public interface QRCode {
    boolean checkQRCode(QrcodeResponse fields, String key, String url);

    String getQRCode(List<MerchantGatewaysetting> params);

    String getResponseQRCodeDescription(String responseCode);

    String getResponseQueryQRCodeDescription(String responseCode);

    boolean checkPayment(Map<String, String> fields, String key);

    String getResponseDescription(String vResponseCode);

    String hashKeyQueryTransactionRequest(Map<String, String> fields, String key);

    String hashKeyQueryTransactionResponse(Map<String, String> fields, String key);
}

package com.ezpay.main.connection.exception;

import com.ezpay.main.connection.utils.ConnectionKey;

public class MerchantNotActivatedGatewayException extends Exception {
    public MerchantNotActivatedGatewayException() {
        super(ConnectionKey.MERCHANT_NOT_ACTIVATED_GATEWAY);
    }
}

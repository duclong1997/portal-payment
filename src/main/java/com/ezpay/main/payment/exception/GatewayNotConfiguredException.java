package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class GatewayNotConfiguredException extends Exception {
    public GatewayNotConfiguredException() {
        super(PaymentKey.GATEWAY_NOT_CONFIGURED);
    }
}

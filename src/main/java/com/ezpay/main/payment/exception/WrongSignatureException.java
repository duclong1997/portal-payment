package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class WrongSignatureException extends Exception {
    public WrongSignatureException() {
        super(PaymentKey.WRONG_SIGNATURE);
    }
}

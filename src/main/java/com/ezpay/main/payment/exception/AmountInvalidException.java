package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class AmountInvalidException extends Exception {
    public AmountInvalidException() {
        super(PaymentKey.TRANSACTION_AMOUNT_INVALID);
    }
}

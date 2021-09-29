package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class TransactionAmoutInvalidException extends Exception {
    public TransactionAmoutInvalidException() {
        super(PaymentKey.AMOUNT_INVALID);
    }
}

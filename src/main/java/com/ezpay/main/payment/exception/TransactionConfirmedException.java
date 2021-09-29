package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class TransactionConfirmedException extends Exception {
    public TransactionConfirmedException() {
        super(PaymentKey.TRANSACTION_CONFIRMED);
    }
}

package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class TransactionAlreadyExistsException extends Exception {
    public TransactionAlreadyExistsException() {
        super(PaymentKey.TRANSACTION_ALREADY_EXISTS);
    }
}

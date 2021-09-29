package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class TransactionMadeFailedException extends Exception {
    public TransactionMadeFailedException() {
        super(PaymentKey.TRANSACTION_MADE_FAILED);
    }
}

package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class TransactionPaymentedException extends Exception{
    public TransactionPaymentedException() {
        super(PaymentKey.TRANSACTION_PAYMENTED);
    }
}

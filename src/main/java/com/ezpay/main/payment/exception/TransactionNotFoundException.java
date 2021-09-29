package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class TransactionNotFoundException extends QueryTansactionException{
    public TransactionNotFoundException() {
        super(PaymentKey.TRANSACTION_NOT_FOUND);
    }
}

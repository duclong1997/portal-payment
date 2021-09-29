package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class TransactionMadeSuccessException extends Exception {
    public TransactionMadeSuccessException() {
        super(PaymentKey.TRANSACTION_MADE_SUCCESS);
    }
}

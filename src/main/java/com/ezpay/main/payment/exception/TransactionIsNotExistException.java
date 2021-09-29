package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class TransactionIsNotExistException extends Exception {
    public TransactionIsNotExistException() {
        super(PaymentKey.UPDATE_ONEPAY_TRANSACTION_IS_NOT_EXIST);
    }
}

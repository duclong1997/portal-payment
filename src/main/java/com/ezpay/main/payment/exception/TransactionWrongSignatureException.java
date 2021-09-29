package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class TransactionWrongSignatureException extends Exception{
    public TransactionWrongSignatureException() {
        super(PaymentKey.TRNASACTION_WRONG_SIGNATURE);
    }
}

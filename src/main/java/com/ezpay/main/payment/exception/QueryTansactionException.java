package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class QueryTansactionException extends Exception {
    public QueryTansactionException() {
        super(PaymentKey.ERROR);
    }

    public QueryTansactionException(String s) {
        super(s);
    }
}

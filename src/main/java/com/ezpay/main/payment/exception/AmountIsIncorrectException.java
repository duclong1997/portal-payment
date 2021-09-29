package com.ezpay.main.payment.exception;

import com.ezpay.main.payment.utils.PaymentKey;

public class AmountIsIncorrectException extends Exception {
    public AmountIsIncorrectException() {
        super(PaymentKey.AMOUNT_IS_INCORRECT);
    }
}

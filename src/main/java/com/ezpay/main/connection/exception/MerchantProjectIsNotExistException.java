package com.ezpay.main.connection.exception;

import com.ezpay.main.connection.utils.ConnectionKey;

public class MerchantProjectIsNotExistException extends Exception {
    public MerchantProjectIsNotExistException(){
        super(ConnectionKey.MERCHANT_PROJECT_IS_NOT_EXIST);
    }
}

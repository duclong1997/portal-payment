package com.ezpay.main.connection.exception;

import com.ezpay.main.connection.utils.ConnectionKey;

public class MerchantIsExistException extends Exception {
    public MerchantIsExistException(){
        super(ConnectionKey.MERCHANT_IS_EXIST);
    }
}

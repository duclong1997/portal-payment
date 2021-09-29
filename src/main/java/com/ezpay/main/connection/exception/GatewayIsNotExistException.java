package com.ezpay.main.connection.exception;

import com.ezpay.main.connection.utils.ConnectionKey;

public class GatewayIsNotExistException extends Exception {
    public GatewayIsNotExistException() {
        super(ConnectionKey.GATEWAY_IS_NOT_EXIST);
    }
}

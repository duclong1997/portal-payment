package com.ezpay.main.connection.exception;

import com.ezpay.main.connection.utils.ConnectionKey;

public class CreateFailedException extends Exception {
    public CreateFailedException() {
        super(ConnectionKey.FAILED);
    }
}

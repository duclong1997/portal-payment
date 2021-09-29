package com.ezpay.main.connection.exception;

import com.ezpay.main.connection.utils.ConnectionKey;

public class ProjectIsNotExistException extends Exception {
    public ProjectIsNotExistException(){
        super(ConnectionKey.PROJECT_IS_NOT_EXIST);
    }
}

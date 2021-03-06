package com.ezpay.core.model;

public class Res {
    public static final String CODE_SUCCESS = "0";
    public static final String CODE_FAILED = "1";

    private String code;
    private String message;
    private Object data;

    public Res(String message) {
        this(message, null);
    }

    public Res(String code, String message) {
        this(code, message, null);
    }

    public Res(String message, Object data) {
        this(CODE_SUCCESS, message, data);
    }

    public Res(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

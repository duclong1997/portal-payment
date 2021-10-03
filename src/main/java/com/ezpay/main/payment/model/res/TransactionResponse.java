package com.ezpay.main.payment.model.res;

public class TransactionResponse {

    public static final String URL = "url";
    public static final String QR_CODE = "qrCode";

    public static final String GET = "get";
    public static final String POST = "post";
    public static final String PUT = "put";
    public static final String HEAD = "head";
    public static final String DELETE = "delete";
    public static final String PATCH = "patch";
    public static final String OPTIONS = "options";

    private String type;
    private String method;
    private String dataRes;

    public TransactionResponse(String type, String method, String dataRes) {
        this.type = type;
        this.method = method;
        this.dataRes = dataRes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDataRes() {
        return dataRes;
    }

    public void setDataRes(String dataRes) {
        this.dataRes = dataRes;
    }
}

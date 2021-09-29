package com.ezpay.main.payment.model.res;

public class UpdateVnpayResponse {
    private String RspCode;
    private String Message;

    public UpdateVnpayResponse(String rspCode, String message) {
        this.RspCode = rspCode;
        Message = message;
    }

    public String getRspCode() {
        return RspCode;
    }

    public void setRspCode(String rspCode) {
        this.RspCode = rspCode;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}

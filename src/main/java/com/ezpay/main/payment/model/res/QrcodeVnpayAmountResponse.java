package com.ezpay.main.payment.model.res;

public class QrcodeVnpayAmountResponse {
    private String amount;

    public QrcodeVnpayAmountResponse(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}

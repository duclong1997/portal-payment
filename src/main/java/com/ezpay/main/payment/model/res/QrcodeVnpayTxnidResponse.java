package com.ezpay.main.payment.model.res;

public class QrcodeVnpayTxnidResponse {
    private String txnId;

    public QrcodeVnpayTxnidResponse(String txnId) {
        this.txnId = txnId;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }
}

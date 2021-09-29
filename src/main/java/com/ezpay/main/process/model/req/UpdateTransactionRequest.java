package com.ezpay.main.process.model.req;

public class UpdateTransactionRequest {
    private String txnRef;//"TEST_15711104329791837463288",
    private String orderInfo;//"TEST_15711104329791837463288",
    private String transactionNo;//"5711105016056174903009",
    private double amount;//200000,
    private String locale;//"vn",
    private String code;//"0",
    private String message;//"Giao dịch thành công",
    private String currencyCode;//"VND",
    private String payDate;//"20191015151740",
    private String gatewayCode;//"ONEPAY_DOM"

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getGatewayCode() {
        return gatewayCode;
    }

    public void setGatewayCode(String gatewayCode) {
        this.gatewayCode = gatewayCode;
    }
}

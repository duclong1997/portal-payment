package com.ezpay.main.payment.model.req;

public class CreateRequest {
    // 0 : Sử dụng máy tính, 1: Sử dụng điện thoại
    private String windowType = "0";
    // default: #ef5459
    private String windowColor = "#ef5459";
    private String orderInfo;
    private String amount;
    private String expDate;
    private String description;
    private String gatewayCode;
    private String ipAddress;
    private String lang = "vi";
    private String returnUrl;
    private String againLink;
    private String cancelUrl;

    public String getWindowType() {
        return windowType;
    }

    public void setWindowType(String windowType) {
        this.windowType = windowType;
    }

    public String getWindowColor() {
        return windowColor;
    }

    public void setWindowColor(String windowColor) {
        this.windowColor = windowColor;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGatewayCode() {
        return gatewayCode;
    }

    public void setGatewayCode(String gatewayCode) {
        this.gatewayCode = gatewayCode;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getAgainLink() {
        return againLink;
    }

    public void setAgainLink(String againLink) {
        this.againLink = againLink;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }
}

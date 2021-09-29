package com.ezpay.main.payment.model.req;

public class VnpayAddDataRequest {
    private String merchantType;// "5045",
    private String serviceCode;// "06",
    private String masterMerCode;// "A000000775",
    private String merchantCode;// "0311609355",
    private String terminalId;// "FPT02",
    private String productId;// "",
    private String amount;// "100000",
    private String ccy;// "704",
    private String qty;// "1",
    private String note;// ""

    public String getMerchantType() {
        return merchantType;
    }

    public void setMerchantType(String merchantType) {
        this.merchantType = merchantType;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getMasterMerCode() {
        return masterMerCode;
    }

    public void setMasterMerCode(String masterMerCode) {
        this.masterMerCode = masterMerCode;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

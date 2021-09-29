package com.ezpay.main.payment.model.req;

public class UpdateMegaPayRequest {
    private String resultCd;
    private String resultMsg;
    private String merId;
    private String trxId;
    private String invoiceNo;
    private String amount;
    private String bankId;
    private String status;  // 0: thanh toán  1: Void   2: Refund
    private String cardNo;
    private String currency;
    private String goodsNm;
    private String buyerFirstNm;
    private String buyerLastNm;
    private String payType;  // DC: Thanh toán bằng thẻ ATM nội địa   IC: Thanh toán bằng thẻ tín dụng  VA: Thanh toán bằng Tài khoản chuyên dụng
    private String trxDt;   // yyyymmdd
    private String trxTm;
    private String timeStamp;
    private String merTrxId;
    private String merchantToken;

    public String getResultCd() {
        return resultCd;
    }

    public void setResultCd(String resultCd) {
        this.resultCd = resultCd;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getGoodsNm() {
        return goodsNm;
    }

    public void setGoodsNm(String goodsNm) {
        this.goodsNm = goodsNm;
    }

    public String getBuyerFirstNm() {
        return buyerFirstNm;
    }

    public void setBuyerFirstNm(String buyerFirstNm) {
        this.buyerFirstNm = buyerFirstNm;
    }

    public String getBuyerLastNm() {
        return buyerLastNm;
    }

    public void setBuyerLastNm(String buyerLastNm) {
        this.buyerLastNm = buyerLastNm;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getTrxDt() {
        return trxDt;
    }

    public void setTrxDt(String trxDt) {
        this.trxDt = trxDt;
    }

    public String getTrxTm() {
        return trxTm;
    }

    public void setTrxTm(String trxTm) {
        this.trxTm = trxTm;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMerTrxId() {
        return merTrxId;
    }

    public void setMerTrxId(String merTrxId) {
        this.merTrxId = merTrxId;
    }

    public String getMerchantToken() {
        return merchantToken;
    }

    public void setMerchantToken(String merchantToken) {
        this.merchantToken = merchantToken;
    }
}

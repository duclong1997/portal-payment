package com.ezpay.main.payment.model.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MegaPayFormBuilderResponse {

    private String merId = "EPAY000001";

    private String currency = "VND";

    private String amount; // TODO: need validate (number type wanna be)

    private String invoiceNo; // Mã đơn hàng của Merchant, duy nhất trên MegaPay

    private String goodsNm; // Tên sản phẩm

    /*
     * - IC: Thẻ tín dụng (Visa/master/JCB…)
     * - DC: Thẻ ATM nội địa
     * - VA: Tài khoản chuyên dụng
     * - EW: Ví điện tử(Zalopay, Momo, Moca)
     * - IS: Thanh toán trả góp
     * - NO: Không chọn phương thức thanh toán (Người dùng sẽ chọn khi vào trang thanh toán của MegaPay)
     * */
    private String payType;

    private String cardTypeValue; // Loại thẻ

    private String buyerFirstNm;
    private String buyerLastNm;
    private String buyerPhone;
    private String buyerEmail;
    private String buyerAddr;
    private String buyerCity;
    private String buyerState; // Bang/Tỉnh
    private String buyerPostCd; // Mã POST
    private String buyerCountry;

    private String receiverLastNm;
    private String receiverFirstNm;
    private String receiverPhone;
    private String receiverAddr;
    private String receiverCity;
    private String receiverState;
    private String receiverPostCd;
    private String receiverCountry;

    private String callBackUrl; // Trang nhận kết quả thanh toán (Nhận kết quả redirect từ Megapay)
    private String notiUrl; // URL IPN nhận kết quả Merchant
    private String reqDomain; // Website sử dụng Megapay làm công cụ thanh toán

    private String vat;
    private String fee;
    private String notax;
    private String description;

    @NotNull
    @NotBlank
    private String merchantToken;

    private String cardTypeToken;

    @JsonProperty("reqServerIP")
    private String reqServerIp;

    private String reqClientVer;

    @JsonProperty("userIp")
    private String userIp;

    @JsonProperty("userSessionID")
    private String userSessionId;

    private String userAgent;

    private String userLanguage = "VN";

    private String timeStamp; // Format: yyyyMMddHHmmss
    private String domesticToken;
    private String payOption;
    private String payToken;
    private String instmntType;
    private String instmntMon;
    private String merTrxId; // merId + unique number

    private String windowColor = "#ef5459";

    private String windowType = "0";

    private String vaStartDt;
    private String vaEndDt;
    private String vaCondition;

    @JsonProperty("mer_temp01")
    private String merTemp01;

    @JsonProperty("mer_temp02")
    private String merTemp02;

    private String bankCode;
    private String subappid; // Bắt buộc nếu thanh toán ZaloPay cho merchant games


    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getGoodsNm() {
        return goodsNm;
    }

    public void setGoodsNm(String goodsNm) {
        this.goodsNm = goodsNm;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getCardTypeValue() {
        return cardTypeValue;
    }

    public void setCardTypeValue(String cardTypeValue) {
        this.cardTypeValue = cardTypeValue;
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

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getBuyerAddr() {
        return buyerAddr;
    }

    public void setBuyerAddr(String buyerAddr) {
        this.buyerAddr = buyerAddr;
    }

    public String getBuyerCity() {
        return buyerCity;
    }

    public void setBuyerCity(String buyerCity) {
        this.buyerCity = buyerCity;
    }

    public String getBuyerState() {
        return buyerState;
    }

    public void setBuyerState(String buyerState) {
        this.buyerState = buyerState;
    }

    public String getBuyerPostCd() {
        return buyerPostCd;
    }

    public void setBuyerPostCd(String buyerPostCd) {
        this.buyerPostCd = buyerPostCd;
    }

    public String getBuyerCountry() {
        return buyerCountry;
    }

    public void setBuyerCountry(String buyerCountry) {
        this.buyerCountry = buyerCountry;
    }

    public String getReceiverLastNm() {
        return receiverLastNm;
    }

    public void setReceiverLastNm(String receiverLastNm) {
        this.receiverLastNm = receiverLastNm;
    }

    public String getReceiverFirstNm() {
        return receiverFirstNm;
    }

    public void setReceiverFirstNm(String receiverFirstNm) {
        this.receiverFirstNm = receiverFirstNm;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddr() {
        return receiverAddr;
    }

    public void setReceiverAddr(String receiverAddr) {
        this.receiverAddr = receiverAddr;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverState() {
        return receiverState;
    }

    public void setReceiverState(String receiverState) {
        this.receiverState = receiverState;
    }

    public String getReceiverPostCd() {
        return receiverPostCd;
    }

    public void setReceiverPostCd(String receiverPostCd) {
        this.receiverPostCd = receiverPostCd;
    }

    public String getReceiverCountry() {
        return receiverCountry;
    }

    public void setReceiverCountry(String receiverCountry) {
        this.receiverCountry = receiverCountry;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public String getNotiUrl() {
        return notiUrl;
    }

    public void setNotiUrl(String notiUrl) {
        this.notiUrl = notiUrl;
    }

    public String getReqDomain() {
        return reqDomain;
    }

    public void setReqDomain(String reqDomain) {
        this.reqDomain = reqDomain;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getNotax() {
        return notax;
    }

    public void setNotax(String notax) {
        this.notax = notax;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMerchantToken() {
        return merchantToken;
    }

    public void setMerchantToken(String merchantToken) {
        this.merchantToken = merchantToken;
    }

    public String getCardTypeToken() {
        return cardTypeToken;
    }

    public void setCardTypeToken(String cardTypeToken) {
        this.cardTypeToken = cardTypeToken;
    }

    public String getReqServerIp() {
        return reqServerIp;
    }

    public void setReqServerIp(String reqServerIp) {
        this.reqServerIp = reqServerIp;
    }

    public String getReqClientVer() {
        return reqClientVer;
    }

    public void setReqClientVer(String reqClientVer) {
        this.reqClientVer = reqClientVer;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(String userSessionId) {
        this.userSessionId = userSessionId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserLanguage() {
        return userLanguage;
    }

    public void setUserLanguage(String userLanguage) {
        this.userLanguage = userLanguage;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDomesticToken() {
        return domesticToken;
    }

    public void setDomesticToken(String domesticToken) {
        this.domesticToken = domesticToken;
    }

    public String getPayOption() {
        return payOption;
    }

    public void setPayOption(String payOption) {
        this.payOption = payOption;
    }

    public String getPayToken() {
        return payToken;
    }

    public void setPayToken(String payToken) {
        this.payToken = payToken;
    }

    public String getInstmntType() {
        return instmntType;
    }

    public void setInstmntType(String instmntType) {
        this.instmntType = instmntType;
    }

    public String getInstmntMon() {
        return instmntMon;
    }

    public void setInstmntMon(String instmntMon) {
        this.instmntMon = instmntMon;
    }

    public String getMerTrxId() {
        return merTrxId;
    }

    public void setMerTrxId(String merTrxId) {
        this.merTrxId = merTrxId;
    }

    public String getWindowColor() {
        return windowColor;
    }

    public void setWindowColor(String windowColor) {
        this.windowColor = windowColor;
    }

    public String getWindowType() {
        return windowType;
    }

    public void setWindowType(String windowType) {
        this.windowType = windowType;
    }

    public String getVaStartDt() {
        return vaStartDt;
    }

    public void setVaStartDt(String vaStartDt) {
        this.vaStartDt = vaStartDt;
    }

    public String getVaEndDt() {
        return vaEndDt;
    }

    public void setVaEndDt(String vaEndDt) {
        this.vaEndDt = vaEndDt;
    }

    public String getVaCondition() {
        return vaCondition;
    }

    public void setVaCondition(String vaCondition) {
        this.vaCondition = vaCondition;
    }

    public String getMerTemp01() {
        return merTemp01;
    }

    public void setMerTemp01(String merTemp01) {
        this.merTemp01 = merTemp01;
    }

    public String getMerTemp02() {
        return merTemp02;
    }

    public void setMerTemp02(String merTemp02) {
        this.merTemp02 = merTemp02;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getSubappid() {
        return subappid;
    }

    public void setSubappid(String subappid) {
        this.subappid = subappid;
    }
}

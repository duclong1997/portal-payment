package com.ezpay.main.payment.model.req;

import java.util.List;

public class UpdateQrcodeVnpayRequest {
    private String code;// "00",
    private String message;// "Tru tien thanh cong, so trace 100550",
    private String msgType;// "1",
    private String txnId;// "50141",
    private String qrTrace;// "000098469",
    private String bankCode;// "VIETCOMBANK",
    private String mobile;// "0989511021",
    private String accountNo;// "",
    private String amount;// "100000",
    private String payDate;// "20180807164732",
    private String masterMerCode;// "A000000775",
    private String merchantCode;// "0311609355",
    private String terminalId;// "FPT02",
    private List<VnpayAddDataRequest> addData;//
    private String checksum;// "81F77683FEA4EBE2CE748AFC99CC3AE9",
    private String ccy;// "704",
    private String secretKey;// "VNPAY"

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

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getQrTrace() {
        return qrTrace;
    }

    public void setQrTrace(String qrTrace) {
        this.qrTrace = qrTrace;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
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

    public List<VnpayAddDataRequest> getAddData() {
        return addData;
    }

    public void setAddData(List<VnpayAddDataRequest> addData) {
        this.addData = addData;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}

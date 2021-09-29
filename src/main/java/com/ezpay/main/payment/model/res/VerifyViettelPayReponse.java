package com.ezpay.main.payment.model.res;

public class VerifyViettelPayReponse {
    private String billcode;// "BILL_123",
    private String error_code;// "00",
    private String merchant_code;// "MSTT",
    private String order_id;// "MSTT01234",
    private String trans_amount;// "3750000",
    private String check_sum;// "AcirUAX2 pvEnIZ8Q85aAL6NGJs="

    public VerifyViettelPayReponse(String error_code) {
        this.error_code = error_code;
    }

    public VerifyViettelPayReponse() {
    }

    public VerifyViettelPayReponse(String billcode, String error_code, String merchant_code, String order_id, String trans_amount, String check_sum) {
        this.billcode = billcode;
        this.error_code = error_code;
        this.merchant_code = merchant_code;
        this.order_id = order_id;
        this.trans_amount = trans_amount;
        this.check_sum = check_sum;
    }

    public String getBillcode() {
        return billcode;
    }

    public void setBillcode(String billcode) {
        this.billcode = billcode;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getMerchant_code() {
        return merchant_code;
    }

    public void setMerchant_code(String merchant_code) {
        this.merchant_code = merchant_code;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getTrans_amount() {
        return trans_amount;
    }

    public void setTrans_amount(String trans_amount) {
        this.trans_amount = trans_amount;
    }

    public String getCheck_sum() {
        return check_sum;
    }

    public void setCheck_sum(String check_sum) {
        this.check_sum = check_sum;
    }
}

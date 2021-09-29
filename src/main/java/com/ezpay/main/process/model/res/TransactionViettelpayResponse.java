package com.ezpay.main.process.model.res;

public class TransactionViettelpayResponse {
    private String merchant_code;
    private String order_id;
    private String error_code;
    private String vt_transaction_id;
    private String payment_status;
    private String version;
    private String check_sum;

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

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getVt_transaction_id() {
        return vt_transaction_id;
    }

    public void setVt_transaction_id(String vt_transaction_id) {
        this.vt_transaction_id = vt_transaction_id;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCheck_sum() {
        return check_sum;
    }

    public void setCheck_sum(String check_sum) {
        this.check_sum = check_sum;
    }
}

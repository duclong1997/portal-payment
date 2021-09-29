package com.ezpay.main.payment.model.res;

public class UpdateViettelPayResponse {
    private String error_code;
    private String merchant_code;
    private String order_id;
    private String return_url;
    private String return_bill_code;
    private String return_other_info;
    private String check_sum;

    public UpdateViettelPayResponse(String error_code) {
        this.error_code = error_code;
    }

    public UpdateViettelPayResponse(String error_code, String merchant_code, String order_id, String return_url, String return_bill_code, String return_other_info, String check_sum) {
        this.error_code = error_code;
        this.merchant_code = merchant_code;
        this.order_id = order_id;
        this.return_url = return_url;
        this.return_bill_code = return_bill_code;
        this.return_other_info = return_other_info;
        this.check_sum = check_sum;
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

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getReturn_bill_code() {
        return return_bill_code;
    }

    public void setReturn_bill_code(String return_bill_code) {
        this.return_bill_code = return_bill_code;
    }

    public String getReturn_other_info() {
        return return_other_info;
    }

    public void setReturn_other_info(String return_other_info) {
        this.return_other_info = return_other_info;
    }

    public String getCheck_sum() {
        return check_sum;
    }

    public void setCheck_sum(String check_sum) {
        this.check_sum = check_sum;
    }
}

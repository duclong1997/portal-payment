package com.ezpay.main.payment.model.res;

public class UpdateMegaPayResponse {
    private String resultCd;
    private String resultMsg;

    public UpdateMegaPayResponse(String resultCd, String resultMsg) {
        this.resultCd = resultCd;
        this.resultMsg = resultMsg;
    }

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
}

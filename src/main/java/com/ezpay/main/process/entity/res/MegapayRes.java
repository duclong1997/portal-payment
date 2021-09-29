package com.ezpay.main.process.entity.res;

import com.ezpay.main.process.entity.model.MegapayDataModel;

public class MegapayRes {
    private String resultCd;
    private MegapayDataModel data;
    private String resultMsg;

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getResultCd() {
        return resultCd;
    }

    public void setResultCd(String resultCd) {
        this.resultCd = resultCd;
    }

    public MegapayDataModel getData() {
        return data;
    }

    public void setData(MegapayDataModel data) {
        this.data = data;
    }
}

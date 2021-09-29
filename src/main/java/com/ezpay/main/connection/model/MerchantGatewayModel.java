package com.ezpay.main.connection.model;

import java.util.List;

public class MerchantGatewayModel {
    private String code;
    private String name;
    private boolean active;
    private List<MerchantGatewaysettingModel> params;

    public MerchantGatewayModel() {
    }

    public MerchantGatewayModel(String code, String name, List<MerchantGatewaysettingModel> params) {
        this.code = code;
        this.name = name;
        this.params = params;
    }

    public MerchantGatewayModel(String code, String name, boolean active, List<MerchantGatewaysettingModel> params) {
        this.code = code;
        this.name = name;
        this.active = active;
        this.params = params;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MerchantGatewaysettingModel> getParams() {
        return params;
    }

    public void setParams(List<MerchantGatewaysettingModel> params) {
        this.params = params;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

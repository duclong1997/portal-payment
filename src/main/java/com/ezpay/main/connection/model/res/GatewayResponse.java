package com.ezpay.main.connection.model.res;

import java.util.List;

public class GatewayResponse {
    private String code;
    private String name;
    private List<GatewaysettingResponse> params;

    public GatewayResponse(String code, String name, List<GatewaysettingResponse> params) {
        this.code = code;
        this.name = name;
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

    public List<GatewaysettingResponse> getParams() {
        return params;
    }

    public void setParams(List<GatewaysettingResponse> params) {
        this.params = params;
    }
}

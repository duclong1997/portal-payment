package com.ezpay.main.connection.model.res;

public class GatewaysettingResponse {
    private String key;
    private int type;

    public GatewaysettingResponse() {
    }

    public GatewaysettingResponse(String key, int type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

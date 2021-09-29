package com.ezpay.main.connection.model.res;

public class RegisterResponse {
    private String connectId;
    private String connectKey;

    public RegisterResponse(String connectId, String connectKey) {
        this.connectId = connectId;
        this.connectKey = connectKey;
    }

    public String getConnectId() {
        return connectId;
    }

    public void setConnectId(String connectId) {
        this.connectId = connectId;
    }

    public String getConnectKey() {
        return connectKey;
    }

    public void setConnectKey(String connectKey) {
        this.connectKey = connectKey;
    }
}

package com.ezpay.core.gateway.model.res;

import com.ezpay.core.gateway.model.QrcodeResponse;

public class QrcodeVnpayResponse extends QrcodeResponse {
    private String code;//"00",
    private String message;//"Success",
    private String data;//"00020101021226280010A000000775011001062734485204504553037045408120000005802VN5907EZCLOUD6005HANOI62300314EZCLOUD QRCODE0708EZCLOUD363042A04",
    private String url;//null,
    private String checksum;//"A3E013F7C392FA8C0086973D562D2912",
    private String isDelete;//false,
    private String idQrcode;//"60958",
    private String visa;//null,
    private String master;//null,
    private String unionPay;//null

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getIdQrcode() {
        return idQrcode;
    }

    public void setIdQrcode(String idQrcode) {
        this.idQrcode = idQrcode;
    }

    public String getVisa() {
        return visa;
    }

    public void setVisa(String visa) {
        this.visa = visa;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getUnionPay() {
        return unionPay;
    }

    public void setUnionPay(String unionPay) {
        this.unionPay = unionPay;
    }
}

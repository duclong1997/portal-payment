package com.ezpay.core.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author OI
 */
@Entity
@Table(name = "merchantgatewaysetting")
public class MerchantGatewaysetting implements Serializable {
    public static final int URL_PARAM = 0;
    public static final int KEY_PARAM = 1;
    public static final int FIXED_PARAM = 2;
    public static final int OTHER_PARAM = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String parameter;
    private String value;
    private int type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchantGatewayId")
    private MerchantGateway gateway;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public MerchantGateway getGateway() {
        return gateway;
    }

    public void setGateway(MerchantGateway gateway) {
        this.gateway = gateway;
    }
}

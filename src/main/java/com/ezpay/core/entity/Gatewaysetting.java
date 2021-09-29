package com.ezpay.core.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author OI
 */
@Entity
@Table(name = "gatewaysetting")
public class Gatewaysetting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String parameter;
    private int type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gatewayCode")
    private Gateway gateway;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }
}

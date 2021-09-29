package com.ezpay.core.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author OI
 */
@Entity
@Table(name = "gateway")
public class Gateway implements Serializable {
    @Id
    private String code;
    private String name;
    private boolean active;
    private String despcription;

    @OneToMany(mappedBy = "gateway", cascade = CascadeType.ALL)
    private List<Gatewaysetting> params;

    @OneToMany(mappedBy = "gateway", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

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

    public String getDespcription() {
        return despcription;
    }

    public void setDespcription(String despcription) {
        this.despcription = despcription;
    }

    public List<Gatewaysetting> getParams() {
        return params;
    }

    public void setParams(List<Gatewaysetting> params) {
        this.params = params;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}

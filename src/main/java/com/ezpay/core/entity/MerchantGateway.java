package com.ezpay.core.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author OI
 */
@Entity
@Table(name = "merchantgateway")
public class MerchantGateway implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchantProjectId")
    private MerchantProject merchantProject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gatewayCode")
    private Gateway gateway;

    @OneToMany(mappedBy = "gateway", cascade = CascadeType.ALL)
    private List<MerchantGatewaysetting> params;

    @OneToMany(mappedBy = "merchantGateway", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public MerchantProject getMerchantProject() {
        return merchantProject;
    }

    public void setMerchantProject(MerchantProject merchantProject) {
        this.merchantProject = merchantProject;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }

    public List<MerchantGatewaysetting> getParams() {
        return params;
    }

    public void setParams(List<MerchantGatewaysetting> params) {
        this.params = params;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}

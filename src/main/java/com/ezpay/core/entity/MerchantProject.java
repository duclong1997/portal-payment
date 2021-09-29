package com.ezpay.core.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author OI
 */
@Entity
@Table(name = "merchant_project")
public class MerchantProject implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private boolean active;
    private String connectId;
    private String connectKey;
    private String ipnLink;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedDate;
    private String projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchantId")
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectCode")
    private Project project;

    @OneToMany(mappedBy = "merchantProject", cascade = CascadeType.ALL)
    private List<MerchantGateway> merchantGateways;

    @OneToMany(mappedBy = "merchantProject", cascade = CascadeType.ALL)
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<MerchantGateway> getMerchantGateways() {
        return merchantGateways;
    }

    public void setMerchantGateways(List<MerchantGateway> merchantGateways) {
        this.merchantGateways = merchantGateways;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getIpnLink() {
        return ipnLink;
    }

    public void setIpnLink(String ipnLink) {
        this.ipnLink = ipnLink;
    }
}

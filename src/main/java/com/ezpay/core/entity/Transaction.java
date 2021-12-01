package com.ezpay.core.entity;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "transaction")
public class Transaction implements Serializable {
    protected final static Logger LOGGER = LoggerFactory.getLogger(Transaction.class);
    public static final int STATUS_CREATE_NEW = 0;
    public static final int STATUS_GATEWAY_RESPONDED = 1;
    public static final int STATUS_PROJECT_UPDATE_FAIL = 2;
    public static final int STATUS_PROJECT_UPDATED = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double amount;
    private String orderInfo;
    private String responseCode;
    private String responseMessage;
    private String transactionNo;
    private String transactionDate;
    private String notes;
    private int status;
    private String txnRef;
    private String description;
    private Date expDate;
    private String ipAddress;
    private int countUpdates;
    private int countQuery;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "merchantGatewayId")
    private MerchantGateway merchantGateway;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchantProjectId")
    private MerchantProject merchantProject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gatewayCode")
    private Gateway gateway;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
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

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public MerchantGateway getMerchantGateway() {
        return merchantGateway;
    }

    public void setMerchantGateway(MerchantGateway merchantGateway) {
        this.merchantGateway = merchantGateway;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        LOGGER.info("Update status : " + status);
        this.status = status;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCountUpdates() {
        return countUpdates;
    }

    public void setCountUpdates(int countUpdates) {
        LOGGER.info("Update count update : " + countUpdates);
        this.countUpdates = countUpdates;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getCountQuery() {
        return countQuery;
    }

    public void setCountQuery(int countQuery) {
        LOGGER.info("Update count query : " + countQuery);
        this.countQuery = countQuery;
    }
}

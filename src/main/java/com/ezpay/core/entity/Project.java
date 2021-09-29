package com.ezpay.core.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author OI
 */
@Entity
@Table(name = "project")
public class Project implements Serializable {
    @Id
    private String code;
    private boolean active;
    private String name;
    private String description;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<MerchantProject> merchantProjects;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MerchantProject> getMerchantProjects() {
        return merchantProjects;
    }

    public void setMerchantProjects(List<MerchantProject> merchantProjects) {
        this.merchantProjects = merchantProjects;
    }
}

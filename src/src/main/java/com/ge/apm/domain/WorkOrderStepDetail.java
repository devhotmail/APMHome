/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "work_order_step_detail")
public class WorkOrderStepDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private int siteId;
    @Column(name = "man_hours")
    private Integer manHours;
    @Size(max = 60)
    @Column(name = "accessory")
    private String accessory;
    @Column(name = "accessory_quantity")
    private Integer accessoryQuantity;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "accessory_price")
    private Double accessoryPrice;

    @Column(name = "step_id")
    @Basic(optional = false)
    @NotNull
    private Integer workOrderStepId;

    public WorkOrderStepDetail() {
    }

    public WorkOrderStepDetail(Integer id) {
        this.id = id;
    }

    public WorkOrderStepDetail(Integer id, int siteId) {
        this.id = id;
        this.siteId = siteId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public Integer getManHours() {
        return manHours;
    }

    public void setManHours(Integer manHours) {
        this.manHours = manHours;
    }

    public String getAccessory() {
        return accessory;
    }

    public void setAccessory(String accessory) {
        this.accessory = accessory;
    }

    public Integer getAccessoryQuantity() {
        return accessoryQuantity;
    }

    public void setAccessoryQuantity(Integer accessoryQuantity) {
        this.accessoryQuantity = accessoryQuantity;
    }

    public Double getAccessoryPrice() {
        return accessoryPrice;
    }

    public void setAccessoryPrice(Double accessoryPrice) {
        this.accessoryPrice = accessoryPrice;
    }

    public Integer getWorkOrderStepId() {
        return workOrderStepId;
    }

    public void setWorkOrderStepId(Integer workOrderStepId) {
        this.workOrderStepId = workOrderStepId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkOrderStepDetail)) {
            return false;
        }
        WorkOrderStepDetail other = (WorkOrderStepDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.WorkOrderStepDetail[ id=" + id + " ]";
    }
    
}

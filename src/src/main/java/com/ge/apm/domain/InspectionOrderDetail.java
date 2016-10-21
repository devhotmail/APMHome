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

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "inspection_order_detail")
public class InspectionOrderDetail implements Serializable {

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
    @Basic(optional = false)
    @NotNull
    @Column(name = "dept_id")
    private int deptId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "dept_name")
    private int deptName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "item_id")
    private int itemId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "item_name")
    private int itemName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_passed")
    private boolean isPassed;

    @Basic(optional = false)
    @NotNull
    @Column(name = "order_id")
    private Integer inspectionOrderId;

    public InspectionOrderDetail() {
    }

    public InspectionOrderDetail(Integer id) {
        this.id = id;
    }

    public InspectionOrderDetail(Integer id, int siteId, int deptId, int deptName, int itemId, int itemName, boolean isPassed) {
        this.id = id;
        this.siteId = siteId;
        this.deptId = deptId;
        this.deptName = deptName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.isPassed = isPassed;
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

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public int getDeptName() {
        return deptName;
    }

    public void setDeptName(int deptName) {
        this.deptName = deptName;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemName() {
        return itemName;
    }

    public void setItemName(int itemName) {
        this.itemName = itemName;
    }

    public boolean getIsPassed() {
        return isPassed;
    }

    public void setIsPassed(boolean isPassed) {
        this.isPassed = isPassed;
    }

    public Integer getInspectionOrderId() {
        return inspectionOrderId;
    }

    public void setInspectionOrderId(Integer inspectionOrderId) {
        this.inspectionOrderId = inspectionOrderId;
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
        if (!(object instanceof InspectionOrderDetail)) {
            return false;
        }
        InspectionOrderDetail other = (InspectionOrderDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.InspectionOrderDetail[ id=" + id + " ]";
    }
    
}

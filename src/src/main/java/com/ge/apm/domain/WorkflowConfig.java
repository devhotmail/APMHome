/*
 */
package com.ge.apm.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "workflow_config")
public class WorkflowConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "site_id")
    private int siteId;
    @Basic(optional = false)
    @Column(name = "hospital_id")
    private int hospitalId;
    @Basic(optional = false)
    @Column(name = "dispatch_mode")
    private int dispatchMode;
    @Column(name = "dispatch_user_id")
    private Integer dispatchUserId;
    @Column(name = "dispatch_user_name")
    private String dispatchUserName;
    @Column(name = "timeout_dispatch")
    private Integer timeoutDispatch;
    @Column(name = "timeout_accept")
    private Integer timeoutAccept;
    @Column(name = "timeout_repair")
    private Integer timeoutRepair;
    @Column(name = "timeout_close")
    private Integer timeoutClose;


    @Column(name = "order_reopen_timeframe")
    private Integer orderReopenTimeframe;
    public WorkflowConfig() {
    }

    public WorkflowConfig(Integer id) {
        this.id = id;
    }

    public WorkflowConfig(Integer id, int siteId, int hospitalId, int dispatchMode) {
        this.id = id;
        this.siteId = siteId;
        this.hospitalId = hospitalId;
        this.dispatchMode = dispatchMode;
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

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getDispatchMode() {
        return dispatchMode;
    }

    public void setDispatchMode(int dispatchMode) {
        this.dispatchMode = dispatchMode;
    }

    public Integer getDispatchUserId() {
        return dispatchUserId;
    }

    public void setDispatchUserId(Integer dispatchUserId) {
        this.dispatchUserId = dispatchUserId;
    }

    public String getDispatchUserName() {
        return dispatchUserName;
    }

    public void setDispatchUserName(String dispatchUserName) {
        this.dispatchUserName = dispatchUserName;
    }

    public Integer getTimeoutDispatch() {
        return timeoutDispatch;
    }

    public void setTimeoutDispatch(Integer timeoutDispatch) {
        this.timeoutDispatch = timeoutDispatch;
    }

    public Integer getTimeoutAccept() {
        return timeoutAccept;
    }

    public void setTimeoutAccept(Integer timeoutAccept) {
        this.timeoutAccept = timeoutAccept;
    }

    public Integer getTimeoutRepair() {
        return timeoutRepair;
    }

    public void setTimeoutRepair(Integer timeoutRepair) {
        this.timeoutRepair = timeoutRepair;
    }

    public Integer getTimeoutClose() {
        return timeoutClose;
    }

    public void setTimeoutClose(Integer timeoutClose) {
        this.timeoutClose = timeoutClose;
    }

    public Integer getOrderReopenTimeframe() {
        return orderReopenTimeframe;
    }

    public void setOrderReopenTimeframe(Integer orderReopenTimeframe) {
        this.orderReopenTimeframe = orderReopenTimeframe;
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
        if (!(object instanceof WorkflowConfig)) {
            return false;
        }
        WorkflowConfig other = (WorkflowConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.WorkflowConfig[ id=" + id + " ]";
    }
    
}

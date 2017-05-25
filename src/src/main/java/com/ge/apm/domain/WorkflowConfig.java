package com.ge.apm.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private Integer siteId;
    @Basic(optional = false)
    @Column(name = "hospital_id")
    private Integer hospitalId;
    @Basic(optional = false)
    @Column(name = "dispatch_mode")
    private Integer dispatchMode;
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
    
    @Column(name="order_reopen_timeframe")
    private Integer orderReopenTimeframe;//二次开单的最大时间间隔
    
    @Column(name="max_message_count")
    private Integer maxMessageCount;//推送消息的次数上限
    
    @Column(name = "dispatch_user_id2")
    private Integer dispatchUserId2;
    
    @Column(name = "dispatch_user_name2")
    private String dispatchUserName2;
     
    @Column(name = "is_take_order_enabled")
    private Boolean isTakeOrderEnabled; //是否允许抢单
    
    @Column(name = "auto_ack")
    private Boolean autoAck;
    
    

    public Boolean getAutoAck() {
		return autoAck;
	}

	public void setAutoAck(Boolean autoAck) {
		this.autoAck = autoAck;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public void setDispatchMode(Integer dispatchMode) {
		this.dispatchMode = dispatchMode;
	}

	public Boolean getIsTakeOrderEnabled() {
		return isTakeOrderEnabled;
	}

	public void setIsTakeOrderEnabled(Boolean isTakeOrderEnabled) {
		this.isTakeOrderEnabled = isTakeOrderEnabled;
	}

	public Integer getOrderReopenTimeframe() {
		return orderReopenTimeframe;
	}

	public void setOrderReopenTimeframe(Integer orderReopenTimeframe) {
		this.orderReopenTimeframe = orderReopenTimeframe;
	}

	public Integer getMaxMessageCount() {
		return maxMessageCount;
	}

	public void setMaxMessageCount(Integer maxMessageCount) {
		this.maxMessageCount = maxMessageCount;
	}

	public Integer getDispatchUserId2() {
		return dispatchUserId2;
	}

	public void setDispatchUserId2(Integer dispatchUserId2) {
		this.dispatchUserId2 = dispatchUserId2;
	}

	public String getDispatchUserName2() {
		return dispatchUserName2;
	}

	public void setDispatchUserName2(String dispatchUserName2) {
		this.dispatchUserName2 = dispatchUserName2;
	}

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

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getDispatchMode() {
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

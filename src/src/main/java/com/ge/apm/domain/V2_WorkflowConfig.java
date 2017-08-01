package com.ge.apm.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v2_workflow_config")
public class V2_WorkflowConfig implements Serializable{
	
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "device_type")
    private Integer deviceType;
    
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
    private Boolean autoAck;//是否需要验收
    
    @Column(name = "urgent_timeout_close")
    private Integer urgentTimeout;//紧急工单超时提醒时间
    
    @Column(name="urgent_max_message_count")
    private Integer urgentMaxMessageCount;//紧急工单提醒次数
    
/*    @Column(name = "pm_remind_days")//提前提醒的天数
    private Integer pmRemindDays;
    
    @Column(name = "pm_remind_times")//提醒次数
    private Integer pmRemindTimes;*/
    
    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;
    
    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUID;
    
    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUID;
    
    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUID;
    
    
    public Integer getUrgentTimeout() {
		return urgentTimeout;
	}

	public void setUrgentTimeout(Integer urgentTimeout) {
		this.urgentTimeout = urgentTimeout;
	}

	public Integer getUrgentMaxMessageCount() {
		return urgentMaxMessageCount;
	}

	public void setUrgentMaxMessageCount(Integer urgentMaxMessageCount) {
		this.urgentMaxMessageCount = urgentMaxMessageCount;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	public String getTenantUID() {
		return tenantUID;
	}

	public void setTenantUID(String tenantUID) {
		this.tenantUID = tenantUID;
	}

	public String getInstitutionUID() {
		return institutionUID;
	}

	public void setInstitutionUID(String institutionUID) {
		this.institutionUID = institutionUID;
	}

	public String getHospitalUID() {
		return hospitalUID;
	}

	public void setHospitalUID(String hospitalUID) {
		this.hospitalUID = hospitalUID;
	}

	public String getSiteUID() {
		return siteUID;
	}

	public void setSiteUID(String siteUID) {
		this.siteUID = siteUID;
	}

	public Boolean getAutoAck() {
		return autoAck;
	}

	public void setAutoAck(Boolean autoAck) {
		this.autoAck = autoAck;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("V2_WorkflowConfig [id=");
		builder.append(id);
		builder.append(", dispatchMode=");
		builder.append(dispatchMode);
		builder.append(", dispatchUserId=");
		builder.append(dispatchUserId);
		builder.append(", dispatchUserName=");
		builder.append(dispatchUserName);
		builder.append(", timeoutDispatch=");
		builder.append(timeoutDispatch);
		builder.append(", timeoutAccept=");
		builder.append(timeoutAccept);
		builder.append(", timeoutRepair=");
		builder.append(timeoutRepair);
		builder.append(", timeoutClose=");
		builder.append(timeoutClose);
		builder.append(", orderReopenTimeframe=");
		builder.append(orderReopenTimeframe);
		builder.append(", maxMessageCount=");
		builder.append(maxMessageCount);
		builder.append(", dispatchUserId2=");
		builder.append(dispatchUserId2);
		builder.append(", dispatchUserName2=");
		builder.append(dispatchUserName2);
		builder.append(", isTakeOrderEnabled=");
		builder.append(isTakeOrderEnabled);
		builder.append(", autoAck=");
		builder.append(autoAck);
		builder.append("]");
		return builder.toString();
	}
    
    
}

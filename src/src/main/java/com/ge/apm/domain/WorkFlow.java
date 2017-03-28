package com.ge.apm.domain;

import java.util.Date;

public class WorkFlow {
	private Integer siteId;
	private Integer hospitalId;
	private Integer workOrderId;
	private Integer dispatchUserId;
	private Integer timeoutDispatch;
	private Integer timeoutAccept;
	private Integer timeoutRepair;
	private Integer timeoutClose;
	
    private Integer currentStepId;
    private Integer currentPersonId;
    
    private Integer ownerId;//work_order_step owner_id
    private Date startTime;//work_order_step start_time
    
	public Integer getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Integer getSiteId() {
		return siteId;
	}
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Integer getWorkOrderId() {
		return workOrderId;
	}
	public void setWorkOrderId(Integer workOrderId) {
		this.workOrderId = workOrderId;
	}
	public Integer getDispatchUserId() {
		return dispatchUserId;
	}
	public void setDispatchUserId(Integer dispatchUserId) {
		this.dispatchUserId = dispatchUserId;
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
	public Integer getCurrentStepId() {
		return currentStepId;
	}
	public void setCurrentStepId(Integer currentStepId) {
		this.currentStepId = currentStepId;
	}
	public Integer getCurrentPersonId() {
		return currentPersonId;
	}
	public void setCurrentPersonId(Integer currentPersonId) {
		this.currentPersonId = currentPersonId;
	}
    
}
